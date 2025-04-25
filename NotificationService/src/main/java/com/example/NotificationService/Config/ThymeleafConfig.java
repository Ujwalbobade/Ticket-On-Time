package com.example.NotificationService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Collections;

@Configuration
public class ThymeleafConfig {
    @Bean
    public TemplateEngine emailTemplateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolvers(Collections.singleton(emailTemplateResolver()));
        return templateEngine;
    }

    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver(){
        ClassLoaderTemplateResolver resolver= new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
