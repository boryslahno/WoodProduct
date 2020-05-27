package com.wood_product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath="C:/Users/borus/IdeaProjects/wood_product/src/main/resources/image/";
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:/"+filePath);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/"+uploadPath);
    }
}
