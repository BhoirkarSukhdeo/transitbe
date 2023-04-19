package com.axisbank.transit.core.config;

import com.axisbank.transit.core.interceptors.AppServiceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorAppConfig implements WebMvcConfigurer {
    @Autowired
    AppServiceInterceptor appServiceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appServiceInterceptor);
    }
}