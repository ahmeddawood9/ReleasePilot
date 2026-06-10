package com.dawood.releasepilot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IntegrationSecurityConfig implements WebMvcConfigurer {

    private final IntegrationTokenInterceptor integrationTokenInterceptor;

    public IntegrationSecurityConfig(IntegrationTokenInterceptor integrationTokenInterceptor) {
        this.integrationTokenInterceptor = integrationTokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(integrationTokenInterceptor)
                .addPathPatterns("/api/integrations/**");
    }
}
