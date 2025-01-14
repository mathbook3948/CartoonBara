package com.pwl.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	response.addHeader("Access-Control-Allow-Origin", "http://localhost:3009"); // 허용 도메인 지정
    	response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Credentials", "true");  // 추가

        
        // OPTIONS 요청에 대한 처리
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
    	//log.info("############### ApiInterceptor: preHandle");
    	
    	HttpSession session = request.getSession(false);

    	String lang = request.getParameter("lang");
    	if(lang == null)
    		lang = "";
    	
    	if(lang.equals("")) {
			
			if(session != null) {		
				if(session.getAttribute("locale") != null) {
					lang = session.getAttribute("locale").toString();
				}
			}
						
			if(lang == null || lang.equals("")) {			
				lang = request.getHeader("Accept-Language");
				if(lang != null && !lang.isEmpty()) {
					lang = request.getLocale().getLanguage();
				}
			}
		}

		if(!"ko".equalsIgnoreCase(lang) && !"en".equalsIgnoreCase(lang)){
			lang = "ko";
		}
    	
    	if(session != null) {
        	session.setAttribute("locale", new Locale(lang));
        }
    	
    	return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    	//log.info("############### ApiInterceptor: postHandle");
    }
    
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		//log.info("############### ApiInterceptor: afterCompletion");
	}
}
