var selPasswordNo = 1;	// 1:password, 2:passwordless, 3:passwordless manage
var timeoutId = null;
var check_millisec = 0;
var passwordless_terms = 0;
var passwordless_milisec = 0;
var pushConnectorUrl = "";
var pushConnectorToken = "";
var sessionId = "";
var loginStatus = false;
var checkType = "";	// 로그인: "LOGIN", QR등록: "QR"

var str_login = "";
var str_cancel = "";
var str_title_password = "";
var str_title_passwordless = "";
var str_passwordless_regunreg = "";
var str_passwordress_notreg = "";
var str_input_id = "";
var str_input_password = "";
var str_passwordless_blocked = "";
var str_login_expired = "";
var str_login_refused = "";
var str_qrreg_expired = "";
var str_passwordless_unreg = "";
var str_try = "";

$(document).ready(function() {
	passwordless = window.localStorage.getItem('passwordless');
	
	if(passwordless != "Y")
		selPassword(1);
	else
		selPassword(2);
	
	$("#id").focus();
})

function trim(stringToTrim) {
	if(stringToTrim != "")
    	return stringToTrim.replaceAll(/^\s+|\s+$/g,"");
    else
    	return stringToTrim;
}

function copyTxt1() {
	var tmpVal = $("#server_url").html();
	tmpVal = tmpVal.replaceAll(" ", "");
	var tempInput = document.createElement("input")
	tempInput.value = tmpVal;
	document.body.appendChild(tempInput);
	tempInput.select();
	document.execCommand("copy");
	document.body.removeChild(tempInput);
	alert(str_serverUrlCopy);
}

function copyTxt2() {
	var tmpVal = $("#register_key").html();
	tmpVal = tmpVal.replaceAll(" ", "");
	var tempInput = document.createElement("input")
	tempInput.value = tmpVal;
	document.body.appendChild(tempInput);
	tempInput.select();
	document.execCommand("copy");
	document.body.removeChild(tempInput);
	alert(str_regCodeCopy);
}

// Password 로그인 & Passwordless 로그인 선택 radio 버튼
function selPassword(sel) {
	if(sel == 1) {
		if(loginStatus == true) {
			cancelLogin();
		}
		
		selPasswordNo = 1;
		$("#login_title").html(str_title_password);
		$("#selLogin1").prop("checked", true);
		$("#selLogin2").prop("checked", false);
		$("#pw").attr("placeholder", "PASSWORD");
		$("#pw").attr("disabled", false);
		$("#pw_group").show();
		$("#bar_group").hide();
		$("#login_bottom1").show();
		$("#login_bottom2").hide();
		
		window.localStorage.removeItem('passwordless');
	}
	else if(sel == 2) {
		selPasswordNo = 2;
		$("#login_title").html(str_title_passwordless);
		$("#selLogin1").prop("checked", false);
		$("#selLogin2").prop("checked", true);
		$("#pw").val("");
		$("#pw").attr("placeholder", "");
		$("#pw").attr("disabled", true);
		$("#pw_group").hide();
		$("#bar_group").show();
		$("#login_bottom1").hide();
		$("#login_bottom2").show();

		window.localStorage.setItem('passwordless', 'Y');
	}
	
	$("#passwordlessSelButton").show();
	$("#manage_bottom").hide();
	$("#passwordlessNotice").hide();
	$("#btn_login").html(str_login);
}

// 로그인 요청
function login() {
	id = $("#id").val();
	pw = $("#pw").val();
	
	id = trim(id);
	pw = trim(pw);
	
	$("#id").val(id);
	$("#pw").val(pw);
	
	if(id == "") {
		alert(str_input_id);	// ID를 입력하세요.
		$("#id").focus();
		return false;
	}

	// Password 로그인
	if(selPasswordNo == 1) {
		if(pw == "") {
			alert(str_input_password);	// PASSWORD를 입력하세요.
			$("#pw").focus();
			return false;
		}
		
		$.ajax({
	        url : "/api/Login/loginCheck",
	        type : "post",
	        data : {
	        	"id" : $('#id').val().trim(),
	            "pw" : $('#pw').val().trim()
	        },
	        async: false,
	        success : function(res) {
	        	if(res.result == "OK") {
	            	location.href = "/main.do";
	        	}
	            else {
	            	alert(res.result);
	            	$("#pw").val("");
	            }
	        },
	        error : function(res) {
	            alert(res.msg);
	        },
	        complete : function() {
	        }
	    });
	}
	// Passwordless 로그인
	else if(selPasswordNo == 2) {
		if(loginStatus == true)
			cancelLogin();
		else
			loginPasswordless();
	}
	// Passwordless manage
	else if(selPasswordNo == 3) {
		managePasswordless();
	}
}

// ------------------------------------------------ Passwordless ------------------------------------------------

function callApi(data) {

	var api_url = "/api/Login/passwordlessCallApi";
	var ret_val = "";
	
	$.ajax({
		url: api_url,
		method: 'POST',
		dataType: 'json',
		data: data,
		async: false,
		success: function(data) {
			//console.log(data);
			ret_val = data;
		},
		error: function(xhr, status, error) {
			console.log("[ERROR] code: " + xhr.status + ", message: " + xhr.responseText + ", status: " + status + ", ERROR: " + error);
		},
		complete: function(data) {
		}
	});
	
	return ret_val;
}

// Passwordless 로그인 요청
function loginPasswordless() {
	checkType = "LOGIN";
	
	var existId = passwordlessCheckID("");
	console.log("existId=" + existId);
	
	if(existId == "T") {
		var token = getTokenForOneTime();
		
		if(token != "") {
			loginStatus = true;
			$("#btn_login").html(str_cancel);	// 취소
			loginPasswordlessStart(token);
		}
	}
	else if(existId == "F") {
		alert(str_passwordress_notreg);	// Passwordless 서비스에 등록되어 있지 않습니다.\nPasswordless 등록이 필요합니다.
	}
	else {
		alert(existId);
	}
}

// 인증기가 등록여부 확인 요청
function passwordlessCheckID(QRReg) {
	var id = $("#id").val();
	var ret_val = "";
	var data = {
		url: "isApUrl",
		params: "userId=" + id + "&QRReg=" + QRReg
	}
	
	var result = callApi(data);
	//console.log(result);
	
	var strResult = result.result;
	if(strResult == "OK") {
		var resultData = result.data;
		var jsonData = JSON.parse(resultData);
		var msg = jsonData.msg;
		var code = jsonData.code;
		
		//console.log("result=" + strResult);
		//console.log("data=" + data);
		//console.log("msg [" + msg + "] code [" + code + "]");
		
		if(code == "000" || code == "000.0") {
			var exist = jsonData.data.exist;
			if(exist)	ret_val = "T";
			else		ret_val = "F";
		}
		else {
			ret_val = msg;
		}	
	}
	else {
		ret_val = strResult;
	}
	
	return ret_val;
}

// onetime token 요청
function getTokenForOneTime() {

	var id = $("#id").val();
	var ret_val = "";
	var data = {
		url: "getTokenForOneTimeUrl",
		params: "userId=" + id
	}
	
	var result = callApi(data);
	var resultData = result.data;
	var jsonData = JSON.parse(resultData);
	var msg = jsonData.msg;
	var code = jsonData.code;
	
	console.log("msg [" + msg + "] code [" + code + "]");
	
	if(code == "000" || code == "000.0") {
		var oneTimeToken = result.oneTimeToken;
		ret_val = oneTimeToken;
	}
	else {
		alert("Onetime Token Request error : [" + code + "] " + msg);
	}

	return ret_val;
}

function loginPasswordlessStart(token) {
	
	var id = $("#id").val();
	var data = {
		url: "getSpUrl",
		params: "userId=" + id + "&token=" + token
	}
	
	var result = callApi(data);
	var resultData = result.data;
	var jsonData = JSON.parse(resultData);
	var msg = jsonData.msg;
	var code = jsonData.code;
	
	console.log("msg [" + msg + "] code [" + code + "]");
	console.log(jsonData.data);
	
	if(code == "000" || code == "000.0") {
		term = jsonData.data.term;
		servicePassword = jsonData.data.servicePassword;
		pushConnectorUrl = jsonData.data.pushConnectorUrl;
		pushConnectorToken = jsonData.data.pushConnectorToken;
		sessionId = result.sessionId;
		
		window.localStorage.setItem('session_id', sessionId);
		
		var today = new Date();
		passwordless_milisec = today.getTime();
		passwordless_terms = parseInt(term - 1);
		console.log("term=" + term + ", servicePassword=" + servicePassword);
		
		connWebSocket();
		drawPasswordlessLogin();
	}
	else if(code == "200.6") {
		sessionId = window.localStorage.getItem('session_id');
		//console.log("Already request authentication --> send [cancel], sessionId=" + sessionId);
		
		if(sessionId !== undefined && sessionId != null && sessionId != "") {
			var data = {
				url: "cancelUrl",
				params: "userId=" + id + "&sessionId=" + sessionId
			}
			
			var result = callApi(data);
			var resultData = result.data;
			var jsonData = JSON.parse(resultData);
			var msg = jsonData.msg;
			var code = jsonData.code;
		
			if(code == "000" || code == "000.0") {
				window.localStorage.removeItem('session_id');
				setTimeout(() => loginPasswordlessStart(token), 500);
			}
			else {
				cancelLogin();
				alert(str_try);	// Try again later.
			}
		}
		else {
			cancelLogin();
			alert(str_try);	// Try again later.
		}
	}
	else if(code == "200.7") {
		cancelLogin();
		alert(str_passwordless_blocked);	// Passwordless 계정이 중지되었습니다.\n계정관리자에게 문의하세요.
	}
}

// OTP code & 타이머 출력
function drawPasswordlessLogin() {
	//console.log("----- drawPasswordlessLogin -----");

	var id = $("#id").val();
	var today = new Date();
	var gap_second = Math.ceil((today.getTime() - passwordless_milisec) / 1000);
	
	if(loginStatus == true) {
		if(gap_second < passwordless_terms) {
		
			var today = new Date();
			var now_millisec = today.getTime();
			var gap_millisec = now_millisec - check_millisec;
			
			if(gap_millisec > 1500) {
				check_millisec = today.getTime();
				//loginPasswordlessCheck();	// polling 방식일때 주석 제거
			}
	
			gap_millisec = now_millisec - passwordless_milisec;
			var ratio = 100 - (gap_millisec / passwordless_terms / 1000) * 100 - 1;
			if(ratio > 0) {
				var tmpPassword = servicePassword;
				if(tmpPassword.length == 6)
					tmpPassword = tmpPassword.substr(0, 3) + " " + tmpPassword.substr(3, 6);
				
				if(loginStatus == true) {
					$("#passwordless_bar").css("width", ratio + "%");
					$("#passwordless_num").text(tmpPassword);
				}
			}
			
			timeoutId = setTimeout(drawPasswordlessLogin, 100);
		}
		else {
			clearTimeout(timeoutId);
			
			$("#rest_time").html("0 : 00");
			
			setTimeout(() => alert(str_login_expired), 100);	// Passwordless 로그인 시간이 만료되었습니다.
			setTimeout(() => cancelLogin(), 100);
		}
	}
}

// 승인 대기
function loginPasswordlessCheck() {
	//console.log("----- loginPasswordlessCheck -----");

	var today = new Date();
	var now_millisec = today.getTime();
	var gap_millisec = now_millisec - passwordless_milisec;
	
	if(gap_millisec < passwordless_terms * 1000 - 1000) {
		
		var id = $("#id").val();
		var data = {
			url: "resultUrl",
			params: "userId=" + id + "&sessionId=" + sessionId
		}
		
		var result = callApi(data);
		var resultData = result.data;
		var jsonData = JSON.parse(resultData);
		var msg = jsonData.msg;
		var code = jsonData.code;
		
		if(code == "000" || code == "000.0") {
			
			var auth = jsonData.data.auth;
			if(auth == "Y") {
				clearTimeout(timeoutId);
				window.localStorage.removeItem('session_id');
				
				//alert("Login OK");
				location.href = "/main.do";
			}
			else if(auth == "N") {
				cancelLogin();
				setTimeout(() => alert(str_login_refused), 100);	// 인증을 거부하였습니다.
			}
		}
	}
}

// 로그인 취소
function cancelLogin() {
	
	loginStatus = false;

	if(timeoutId != null) {
		clearTimeout(timeoutId);
		timeoutId = null;
	}
	
	$("#btn_login").html(str_login);
	$("#passwordless_bar").css("width", "0%");
	$("#passwordless_num").text("--- ---");
	$("#login_mobile_check").hide();

	sessionId = window.localStorage.getItem('session_id');
	
	var id = $("#id").val();
	var data = {
		url: "cancelUrl",
		params: "userId=" + id + "&sessionId=" + sessionId
	}
	
	var result = callApi(data);
	var resultData = result.data;
	var jsonData = JSON.parse(resultData);
	var msg = jsonData.msg;
	var code = jsonData.code;

	window.localStorage.removeItem('session_id');
}

// passwordless 관리페이지 이동
function moveManagePasswordless() {
	selPasswordNo = 3;
	$("#passwordlessSelButton").hide();
	$("#login_bottom1").hide();
	$("#login_bottom2").hide();
	$("#bar_group").hide();
	$("#pw_group").show();
	$("#manage_bottom").show();
	$("#passwordlessNotice").show();
	$("#login_title").html(str_passwordless_regunreg);
	$("#btn_login").html(str_passwordless_regunreg);
	$("#pw").attr("placeholder", "PASSWORD");
	$("#pw").attr("disabled", false);
}

// Passwordless 관리요청
function managePasswordless() {
	
	id = $("#id").val();
	pw = $("#pw").val();
	
	id = trim(id);
	pw = trim(pw);
	
	$("#id").val(id);
	$("#pw").val(pw);
	
	if(id == "") {
		alert(str_input_id);	// ID를 입력하세요.
		$("#id").focus();
		return false;
	}

	if(pw == "") {
		alert(str_input_password);	// PASSWORD를 입력하세요.
		$("#pw").focus();
		return false;
	}
	
	var PasswordlessToken = "";
	
	$.ajax({
        url : "/api/Login/passwordlessManageCheck",
        type : "post",
        data : {
        	"id" : $('#id').val().trim(),
            "pw" : $('#pw').val().trim()
        },
        async: false,
        success : function(res) {
			console.log(res);
        	if(res.result == "OK") {
            	PasswordlessToken = res.PasswordlessToken;
        	}
            else {
            	alert(res.result);
            	$("#pw").val("");
            }
        },
        error : function(res) {
            alert(res.msg);
        },
        complete : function() {
        }
    });
	
	console.log("PasswordlessToken=" + PasswordlessToken);
	$("#passwordlessToken").val(PasswordlessToken);
	
	if(PasswordlessToken != "") {
		var existId = passwordlessCheckID("");
		console.log("existId=" + existId);
		
		if(existId == "T") {
			$("#login_content").hide();
			$("#passwordless_unreg_content").show();
		}
		else {
			getPasswordlessQRinfo(PasswordlessToken);
		}
	}
}

// Passwordless 등록 QR코드 정보요청
function getPasswordlessQRinfo(PasswordlessToken) {
	
	checkType = "QR";

	var id = $("#id").val();
	var data = {
		url: "joinApUrl",
		params: "userId=" + id + "&token=" + PasswordlessToken
	}
	
	var result = callApi(data);
	//console.log(result);
	var resultData = result.data;
	var jsonData = JSON.parse(resultData);
	var msg = jsonData.msg;
	var code = jsonData.code;
	
	console.log(data);
	console.log("msg [" + msg + "] code [" + code + "]");
	
	if(code == "000" || code == "000.0") {
		var data = jsonData.data;
		console.log("------------ info -----------");
		console.log(data);
		
		var data = jsonData.data;
		var qr = data.qr;
		var corpId = data.corpId;
		var registerKey = data.registerKey;
		var terms = data.terms;
		var serverUrl = data.serverUrl;
		var userId = data.userId;
		
		console.log("qr: " + qr);
		console.log("corpId: " + corpId);
		console.log("registerKey: " + registerKey);
		console.log("terms: " + terms);
		console.log("serverUrl: " + serverUrl);
		console.log("userId: " + userId);
		
		pushConnectorUrl = data.pushConnectorUrl;
		pushConnectorToken = data.pushConnectorToken;
		
		console.log("pushConnectorUrl: " + pushConnectorUrl);
		console.log("pushConnectorToken: " + pushConnectorToken);
		
		$("#login_content").hide();
		$("#passwordless_reg_content").show();
		
		var tmpRegisterKey = "";
		var tmpInterval = 4;
		for(var i=0; i<registerKey.length / tmpInterval; i++) {
			tmpRegisterKey = tmpRegisterKey + registerKey.substring(i*tmpInterval, i*tmpInterval + tmpInterval);
			if(registerKey.length > i*tmpInterval)
				tmpRegisterKey = tmpRegisterKey + " ";
		}
		registerKey = tmpRegisterKey;
		
		$("#qr").prop("src", qr);
		$("#user_id").html(userId);
		$("#server_url").html(serverUrl);
		$("#register_key").html(registerKey);
		
		var today = new Date();
		passwordless_milisec = today.getTime();
		passwordless_terms = parseInt(terms - 1);
		check_millisec = today.getTime();
		
		connWebSocket();
		drawPasswordlessReg();
	}
	else {
		alert("[" + code + "] " + msg);
	}
}

function drawPasswordlessReg() {

	var id = $("#userId").val();
	var today = new Date();
	var gap_second = Math.ceil((today.getTime() - passwordless_milisec) / 1000);
	
	if(gap_second < passwordless_terms) {
	
		var tmp_min = parseInt((passwordless_terms - gap_second) / 60);
		var tmp_sec = parseInt((passwordless_terms - gap_second) % 60);
		
		if(tmp_sec < 10)
			tmp_sec = "0" + tmp_sec;
		
		$("#rest_time").html(tmp_min + " : " + tmp_sec);
		
		timeoutId = setTimeout(drawPasswordlessReg, 300);
		
		var today = new Date();
		var now_millisec = today.getTime();
		var gap_millisec = now_millisec - check_millisec;
		if(gap_millisec > 1500) {
			check_millisec = today.getTime();
			//regPasswordlessOK();	// polling 방식일때 주석 제거
		}
	}
	else {
		clearTimeout(timeoutId);
		
		$("#rest_time").html("0 : 00");
		
		$("#login_content").show();
		$("#passwordless_reg_content").hide();
		
		setTimeout(() => alert(str_qrreg_expired), 100);	// Passwordless QR 등록시간이 만료되었습니다.
		setTimeout(() => cancelManage(), 200);
	}
}

// Passwordless 서비스 등록 체크
function regPasswordlessOK() {
	var existId = passwordlessCheckID("T");
	
	if(existId == "T") {
		clearTimeout(timeoutId);
		$("#login_content").hide();
		$("#passwordless_reg_content").show();
	
		cancelManage();
	}
}

// Passwordless 서비스 해지
function unregPasswordless() {
	var passwordlessToken = $("#passwordlessToken").val();
	var id = $("#id").val();
	var data = {
		url: "withdrawalApUrl",
		params: "userId=" + id + "&token=" + passwordlessToken
	}
	
	var result = callApi(data);
	//console.log(result);
	var strResult = result.result;
	if(strResult == "OK") {
		var resultData = result.data;
		var jsonData = JSON.parse(resultData);
		var msg = jsonData.msg;
		var code = jsonData.code;
		
		//console.log("data=" + data);
		//console.log("msg [" + msg + "] code [" + code + "]");
		
		if(code == "000" || code == "000.0") {
			window.localStorage.removeItem('passwordless');
			alert(str_passwordless_unreg);	// Passwordless 서비스가 해지되었습니다.\n\n사용자 Password로 로그인하세요.\n\nPasswordless로 로그인하고 싶다면\nPasswordless 서비스를 먼저 등록하세요.
			selPassword(1);
			cancelManage();
		}
		else {
			cancelManage();
			alert("[" + code + "] " + msg);
		}
	}
	else {
		cancelManage();
		alert(strResult);
	}
}

// 로그인화면으로 이동
function cancelManage() {
	
	if(timeoutId != null) {
		clearTimeout(timeoutId);
		timeoutId = null;
	}

	$("#pw").val("");
	$("#login_content").show();
	$("#passwordless_reg_content").hide();
	$("#passwordless_unreg_content").hide();
	$("#reg_mobile_check").hide();
	
	passwordless = window.localStorage.getItem('passwordless');
	
	if(passwordless != "Y")
		selPassword(1);
	else
		selPassword(2);
}

//도움말
var showHelp = false;
function show_help() {
	if(showHelp == false) {
		$(".pwless_info").show();
		showHelp = true;
	}
	else {
		hide_help();
	}
}
function hide_help() {
	$(".pwless_info").hide();
	showHelp = false;
}

function mobileCheck() {
	if(checkType == "LOGIN")
		loginPasswordlessCheck();
	else if(checkType == "QR")
		regPasswordlessOK();
}

//-------------------------------------------------- WebSocket -------------------------------------------------

/*
	- WebSocket readyState
	  0 CONNECTING	소켓이 생성됐으나 연결은 아직 개방되지 않았습니다.
	  1 OPEN		연결이 개방되어 통신할 수 있습니다.
	  2 CLOSING		연결을 닫는 중입니다.
	  3 CLOSED		연결이 닫혔거나, 개방할 수 없었습니다.
*/

var qrSocket = null;
var result = null;

function connWebSocket() {

	qrSocket = new WebSocket(pushConnectorUrl);

	qrSocket.onopen = function(e) {
		console.log("######## WebSocket Connected ########");
		var send_msg = '{"type":"hand","pushConnectorToken":"' + pushConnectorToken + '"}';
		console.log("url [" + pushConnectorUrl + "]");
		console.log("send [" + send_msg + "]");
		qrSocket.send(send_msg);
	}

	qrSocket.onmessage = async function (event) {
		console.log("######## WebSocket Data received [" + qrSocket.readyState + "] ########");
		
		try {
			if (event !== null && event !== undefined) {
				result = await JSON.parse(event.data);
				if(result.type == "result") {
					if(checkType == "LOGIN")
						loginPasswordlessCheck();
					else if(checkType == "QR")
						regPasswordlessOK();
				}
			}
		} catch (err) {
			console.log(err);
		}
	}

	qrSocket.conclose = function(event) {
		if(event.wasClean)
			console.log("######## WebSocket Disconnected - OK !!! [" + qrSocket.readyState + "] ########");
		else
			console.log("######## WebSocket Disconnected - Error !!! [" + qrSocket.readyState + "] ########");

		console.log("=================================================");
		console.log(event);
		console.log("=================================================");
	}

	qrSocket.onerror = function(error) {
		console.log("######## WebSocket Error !!! [" + qrSocket.readyState + "] ########");
		console.log("=================================================");
		console.log(error);
		console.log("=================================================");

		$("#login_mobile_check").show();
		$("#reg_mobile_check").show();
	}
}