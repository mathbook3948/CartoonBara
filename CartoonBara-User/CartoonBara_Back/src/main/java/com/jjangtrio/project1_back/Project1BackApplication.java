package com.jjangtrio.project1_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication
public class Project1BackApplication {

    public static void main(String[] args) {
        SpringApplication.run(Project1BackApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer crosConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                System.out.println("Cros Allow Origin 실행");
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowedOrigins("http://192.168.0.**:*", "*",
                                "http://192.168.0.**:3009", "http://localhost:3006", "http://localhost:3009",
                                "http://localhost:3002", "http://localhost:3003", "http://localhost:3010",
                                "http://localhost:3011", "http://localhost:3012", "http://localhost:3013", "**")
                        .maxAge(3600);
            }
        };
    }
}