package com.jjangtrio.project1_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:index.html");
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
        // => /경로, /경로/하위경로와 같은 형식의 모든 경로를 index.html로 포워딩
        registry.addViewController("/{path1:[^\\.]*}/{path2:[^\\.]*}").setViewName("forward:/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3009", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*");
    }

    // home/ubuntu/back/uploads/ 를 리소스로 추가한다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/back/uploads/"); // 로컬에서 테스트용
        // .addResourceLocations("file:/usr/local/tomcat/webapps/back/uploads/"); // 배포용
    }
}
