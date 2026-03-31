package com.java.birdme.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set maximum size for a single file (e.g. 20MB)
        factory.setMaxFileSize(20 * 1024 * 1024);
        // Set maximum size for total request (e.g. 20MB)
        factory.setMaxRequestSize(20 * 1024 * 1024);
        return factory.createMultipartConfig();
    }
}
