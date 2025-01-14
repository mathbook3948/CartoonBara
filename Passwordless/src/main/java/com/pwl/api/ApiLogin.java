package com.pwl.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pwl.config.MessageUtils;
import com.pwl.domain.Login.UserInfo;
import com.pwl.jwt.JwtService;
import com.pwl.mapper.Login.LoginMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/Login/")
public class ApiLogin {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private LoginMapper loginMapper;

	@Autowired
	MessageUtils messageUtils;
 
	@Value("${passwordless.corpId}")
	private String corpId;

	@Value("${passwordless.serverId}")
	private String serverId;

	@Value("${passwordless.serverKey}")
	private String serverKey;

	@Value("${passwordless.simpleAutopasswordUrl}")
	private String simpleAutopasswordUrl;

	@Value("${passwordless.restCheckUrl}")
	private String restCheckUrl;

	@Value("${passwordless.pushConnectorUrl}")
	private String pushConnectorUrl;

	@Value("${passwordless.recommend}")
	private String recommend;

	// Passwordless URL
	private String isApUrl = "/ap/rest/auth/isAp"; // Passwordless 등록여부 확인
	private String joinApUrl = "/ap/rest/auth/joinAp"; // Passwordless 등록 REST API
	private String withdrawalApUrl = "/ap/rest/auth/withdrawalAp"; // Passwordless 해지 REST API
	private String getTokenForOneTimeUrl = "/ap/rest/auth/getTokenForOneTime"; // Passwordless 일회용토큰 요청 REST API
	private String getSpUrl = "/ap/rest/auth/getSp"; // Passwordless 인증요청 REST API
	private String resultUrl = "/ap/rest/auth/result"; // Passwordless 인증 결과 요청 REST API
	private String cancelUrl = "/ap/rest/auth/cancel"; // Passwordless 인증요청 취소 REST API

	// 로그인
	@PostMapping(value = "loginCheck", produces = "application/json;charset=utf8")
	public Map<String, Object> loginCheck(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pw", required = false) String pw, HttpServletRequest request) {

		if (id == null)
			id = "";
		if (pw == null)
			pw = "";

		log.info("loginCheck : [" + id + "] / [" + pw + "]");

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if (!id.equals("") && !pw.equals("")) {
			UserInfo userinfo = new UserInfo();
			userinfo.setId(id);
			userinfo.setPw(pw);
			UserInfo newUserinfo = loginMapper.checkPassword(userinfo);

			boolean exist = false;

			if (newUserinfo != null) {
				ModelMap modelMap = passwordlessCallApi("isApUrl", "userId=" + id, request, null);
				log.info("isAp API Response ModelMap: " + modelMap);

				// 먼저 permission을 체크합니다
				int permission = loginMapper.getPermission(userinfo);
				log.info("User Permission: " + permission);

				// permission이 0이면 서비스에 가입되지 않은 것입니다
				if (permission == 0) {
					HttpSession session = request.getSession(true);
					session.setAttribute("id", id);
					mapResult.put("result", "OK");
					return mapResult;
				}

				// permission이 0이 아닐 때만 Passwordless 체크를 진행
				if (modelMap != null) {
					String result = (String) modelMap.getAttribute("result");
					if (result.equals("OK")) {
						String data = (String) modelMap.getAttribute("data");
						log.info("isAp API Response Data: " + data);

						if (data != null && !data.equals("")) {
							JSONParser parser = new JSONParser();
							try {
								JSONObject jsonData = (JSONObject) parser.parse(data);
								log.info("Parsed JSON Data: " + jsonData);

								// Passwordless 서비스 등록 여부를 JSON 응답에서 확인
								boolean isRegistered = false;
								if (jsonData.containsKey("status")) {
									isRegistered = "REGISTERED".equals(jsonData.get("status"));
								} else if (jsonData.containsKey("isRegistered")) {
									isRegistered = (boolean) jsonData.get("isRegistered");
								}

								log.info("Passwordless Registration Status: " + isRegistered);

								if (isRegistered && permission != 0) {
									if (recommend.equals("1")) {
										mapResult.put("result", messageUtils.getMessage("text.passwordless.password"));
										return mapResult;
									}
								}
							} catch (ParseException pe) {
								log.error("JSON Parsing Error", pe);
								mapResult.put("result", "Error parsing Passwordless response");
								return mapResult;
							}
						}
					}
				}

				// 여기까지 왔다면 정상 로그인 처리
				HttpSession session = request.getSession(true);
				session.setAttribute("id", id);
				mapResult.put("result", "OK");
			} else {
				mapResult.put("result", messageUtils.getMessage("text.passwordless.invalid"));
			}
		}

		return mapResult;
	}

	// 로그아웃
	@PostMapping(value = "logout", produces = "application/json;charset=utf8")
	public Map<String, Object> logout(HttpServletRequest request) {

		Map<String, Object> mapResult = new HashMap<String, Object>();
		HttpSession session = request.getSession(true);
		String id = (String) session.getAttribute("id");
		log.info("logout : [" + id + "]");

		session.setAttribute("id", null);
		mapResult.put("result", "OK");

		log.info("logout : [" + id + "] completed.");

		return mapResult;
	}

	// ------------------------------------------------ Passwordless
	// ------------------------------------------------

	// 로그인
	@PostMapping(value = "passwordlessManageCheck", produces = "application/json;charset=utf8")
	public Map<String, Object> passwordlessManageCheck(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pw", required = false) String pw, HttpServletRequest request) {

		if (id == null)
			id = "";
		if (pw == null)
			pw = "";

		log.info("passwordlessManageCheck : id [" + id + "] pw [" + pw + "]");

		Map<String, Object> mapResult = new HashMap<String, Object>();

		if (!id.equals("") && !pw.equals("")) {

			UserInfo userinfo = new UserInfo();
			userinfo.setId(id);

			UserInfo dbUser = loginMapper.getUserById(userinfo);

			if (dbUser != null) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				// 입력된 평문 비밀번호와 DB의 암호화된 비밀번호를 matches()로 비교
				try {
					if (!encoder.matches(pw, dbUser.getPw())) {
						return null;
					}
				} catch (Exception e) {

				}
			}

			if (dbUser != null) {
				String tmpToken = java.util.UUID.randomUUID().toString();
				String tmpTime = Long.toString(System.currentTimeMillis());

				log.info("passwordlessManageCheck : token [" + tmpToken + "] time [" + tmpTime + "]");

				HttpSession session = request.getSession(true);
				session.setAttribute("PasswordlessToken", tmpToken);
				session.setAttribute("PasswordlessTime", tmpTime);

				mapResult.put("PasswordlessToken", tmpToken);
				mapResult.put("result", "OK");
			} else {
				mapResult.put("result", messageUtils.getMessage("text.passwordless.invalid")); // Invalid id or
																								// password.
			}
		} else {
			mapResult.put("result", messageUtils.getMessage("text.passwordless.empty")); // ID or Password is empty.
		}

		return mapResult;
	}

	@RequestMapping(value = "/passwordlessCallApi")
	public ModelMap passwordlessCallApi(@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "params", required = false) String params, HttpServletRequest request,
			HttpServletResponse response) {

		ModelMap modelMap = new ModelMap();
		String result = "";

		boolean existMember = false;

		if (url == null)
			url = "";
		if (params == null)
			params = "";

		Map<String, String> mapParams = getParamsKeyValue(params);

		String userId = "";
		String userToken = "";

		HttpSession session = request.getSession();
		String sessionUserToken = (String) session.getAttribute("PasswordlessToken");
		String sessionTime = (String) session.getAttribute("PasswordlessTime");

		if (sessionUserToken == null)
			sessionUserToken = "";
		if (sessionTime == null)
			sessionTime = "";

		long nowTime = System.currentTimeMillis();
		long tokenTime = 0L;
		int gapTime = 0;
		try {
			tokenTime = Long.parseLong(sessionTime);
			gapTime = (int) (nowTime - tokenTime);
		} catch (Exception e) {
			gapTime = 99999999;
		}

		userId = mapParams.get("userId");
		userToken = mapParams.get("token");

		boolean matchToken = false;
		if (!sessionUserToken.equals("") && sessionUserToken.equals(userToken))
			matchToken = true;

		log.info(
				"passwordlessCallApi : [" + url + "] userId=" + userId + ", Token Match [" + matchToken + "] userToken["
						+ userToken + "], sessionUserToken [" + sessionUserToken + "], gapTime [" + gapTime + "]");

		if (userId == null)
			userId = "";
		if (userToken == null)
			userToken = "";

		// QR 요청 및 해제 시 본인 확인
		if (url.equals("joinApUrl") || url.equals("withdrawalApUrl")) {
			// Passwordless 설정을 위한 로그인이 안된 경우
			if (!matchToken) {
				modelMap.put("result", messageUtils.getMessage("text.passwordless.abnormal")); // This is not a normal
																								// user.
				return modelMap;
			}
			// Passwordless 설정을 위한 로그인 후 5분 경과 시 Timeout 처리
			else if (gapTime > 5 * 60 * 1000) {
				modelMap.put("result", messageUtils.getMessage("text.passwordless.expired")); // Passwordless management
																								// token expired.
				return modelMap;
			}
		}

		if (!url.equals("resultUrl")) {
			log.info("passwordlessCallApi : url [" + url + "] params [" + params + "] userId [" + userId + "]");
		}

		UserInfo userinfo = new UserInfo();
		userinfo.setId(userId);
		UserInfo newUserinfo = loginMapper.getUserById(userinfo);

		if (newUserinfo == null) {
			String tmp_result = messageUtils.getMessage("text.passwordless.idnotexist"); // ID [" + id + "] does not
																							// exist.
			modelMap.put("result", tmp_result.replace("@@@", userId));
			return modelMap;
		} else {
			String random = java.util.UUID.randomUUID().toString();
			String sessionId = System.currentTimeMillis() + "_sessionId";
			String apiUrl = "";
			String ip = request.getRemoteAddr();

			if (ip.equals("0:0:0:0:0:0:0:1"))
				ip = "127.0.0.1";

			if (url.equals("isApUrl")) {
				apiUrl = isApUrl;
			}
			if (url.equals("joinApUrl")) {
				apiUrl = joinApUrl;
			}
			if (url.equals("withdrawalApUrl")) {
				apiUrl = withdrawalApUrl;
			}
			if (url.equals("getTokenForOneTimeUrl")) {
				apiUrl = getTokenForOneTimeUrl;
			}
			if (url.equals("getSpUrl")) {
				apiUrl = getSpUrl;
				params += "&clientIp=" + ip + "&sessionId=" + sessionId + "&random=" + random + "&password=";
			}
			if (url.equals("resultUrl")) {
				apiUrl = resultUrl;
			}
			if (url.equals("cancelUrl")) {
				apiUrl = cancelUrl;
			}

			if (!url.equals("resultUrl")) {
				log.info("passwordlessCallApi : url [" + url + "], param [" + params + "], apiUrl [" + apiUrl + "]");
			}

			if (!apiUrl.equals("")) {
				try {
					if (!url.equals("getSpUrl") && !url.equals("resultUrl")) {
						log.info("passwordlessCallApi : url [" + (restCheckUrl + apiUrl + "?" + params) + "]");
					}

					result = callApi("POST", restCheckUrl + apiUrl, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (!url.equals("getSpUrl") && !url.equals("resultUrl")) {
				log.info("passwordlessCallApi : result [" + result + "]");
			}

			// 1회용 토큰 요청
			if (url.equals("getTokenForOneTimeUrl")) {
				String token = "";
				String oneTimeToken = "";

				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonResponse = (JSONObject) parser.parse(result);
					JSONObject jsonData = (JSONObject) jsonResponse.get("data");
					token = (String) (jsonData).get("token");
					oneTimeToken = getDecryptAES(token, serverKey.getBytes());
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
				log.info("passwordlessCallApi : token [" + token + "] --> oneTimeToken [" + oneTimeToken + "]");

				modelMap.put("oneTimeToken", oneTimeToken);
			}

			// Passwordless 인증요청 REST API
			if (url.equals("getSpUrl")) {
				modelMap.put("sessionId", sessionId);
			}

			// Passwordless QR 등록대기 Websocket URL
			if (url.equals("joinApUrl")) {
				modelMap.put("pushConnectorUrl", pushConnectorUrl);
			}

			// Passwordless QR 등록대기
			if (url.equals("isApUrl")) {
				try {
					int permission = loginMapper.getPermission(userinfo);
					log.info("permission: " + permission);

					String isQRReg = mapParams.get("QRReg");
					if (isQRReg != null && isQRReg.equals("T")) { // QR 등록 확인 시에만
						log.info("passwordlessCallApi : QR등록완료 --> 비밀번호 변경");

						BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
						String newPw = encoder.encode(Long.toString(System.currentTimeMillis()) + ":" + userId);
						userinfo.setId(userId);
						userinfo.setPw(newPw);
						loginMapper.changepw(userinfo);

						// permission 업데이트
						Map<String, Object> map = new HashMap<>();
						map.put("userId", userinfo.getId());
						map.put("permissionNum", 1L);
						loginMapper.updatePermission(map);
					} else if (permission == 0) {
						// 미등록 상태 응답
						modelMap.put("result", "OK");
						JSONObject jsonResponse = new JSONObject();
						jsonResponse.put("isRegistered", false);
						modelMap.put("data", jsonResponse.toJSONString());
						return modelMap;
					}
				} catch (NullPointerException npe) {
					log.error("NullPointerException in isApUrl", npe);
				}
			}

			// Passwordless 승인 대기
			if (url.equals("resultUrl")) {
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonResponse = (JSONObject) parser.parse(result);
					JSONObject jsonData = (JSONObject) jsonResponse.get("data");

					if (jsonData != null) {
						String auth = (String) (jsonData).get("auth");

						// ApiLogin.java의 resultUrl 처리 부분
						if (auth != null && auth.equals("Y")) {
							log.info("passwordlessCallApi : 로그인성공 --> 비밀번호 변경");

							BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
							String newPw = encoder.encode(Long.toString(System.currentTimeMillis()) + ":" + userId);

							userinfo = new UserInfo();
							userinfo.setId(userId);
							userinfo.setPw(newPw);
							loginMapper.changepw(userinfo);

							session.setAttribute("id", userId);
							String jwtToken = jwtService.createToken(userinfo);

							// JWT 토큰을 최상위 레벨에 추가
							modelMap.put("JwtToken", jwtToken);
							modelMap.put("result", "OK");
							modelMap.put("data", result);

							log.info("Generated JWT Token: " + jwtToken); // 디버깅을 위한 로그 추가
						}
					}
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
			}

			modelMap.put("result", "OK");
		}

		modelMap.put("data", result);

		return modelMap;
	}

	public String callApi(String type, String requestURL, String params) {

		String retVal = "";
		Map<String, String> mapParams = getParamsKeyValue(params);

		try {
			URIBuilder b = new URIBuilder(requestURL);

			Set<String> set = mapParams.keySet();
			Iterator<String> keyset = set.iterator();
			while (keyset.hasNext()) {
				String key = keyset.next();
				String value = mapParams.get(key);
				b.addParameter(key, value);
			}
			URI uri = b.build();

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			org.apache.http.HttpResponse response;

			if (type.toUpperCase().equals("POST")) {
				HttpPost httpPost = new HttpPost(uri);
				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
				response = httpClient.execute(httpPost);
			} else {
				HttpGet httpGet = new HttpGet(uri);
				httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
				response = httpClient.execute(httpGet);
			}

			HttpEntity entity = response.getEntity();
			retVal = EntityUtils.toString(entity);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return retVal;
	}

	public Map<String, String> getParamsKeyValue(String params) {
		String[] arrParams = params.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : arrParams) {
			String name = "";
			String value = "";

			String[] tmpArr = param.split("=");
			name = tmpArr[0];

			if (tmpArr.length == 2)
				value = tmpArr[1];

			map.put(name, value);
		}

		return map;
	}

	private static String getDecryptAES(String encrypted, byte[] key) {
		String strRet = null;

		byte[] strIV = key;
		if (key == null || strIV == null)
			return null;
		try {
			SecretKey secureKey = new SecretKeySpec(key, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(strIV));
			byte[] byteStr = java.util.Base64.getDecoder().decode(encrypted);// Base64Util.getDecData(encrypted);
			strRet = new String(c.doFinal(byteStr), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strRet;
	}
}
