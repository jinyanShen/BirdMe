package com.java.birdme.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set maximum size for a single file (e.g. 20MB)
        factory.setMaxFileSize(DataSize.ofBytes(20 * 1024 * 1024));
        // Set maximum size for total request (e.g. 20MB)
        factory.setMaxRequestSize(DataSize.ofBytes(20 * 1024 * 1024));
        return factory.createMultipartConfig();
    }
}