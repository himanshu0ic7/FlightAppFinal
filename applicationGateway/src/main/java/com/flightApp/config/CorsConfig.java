package com.flightApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 2. REQUIRED FOR COOKIES
        corsConfig.setAllowCredentials(true);
        
        // 1. Allow your UI's origin (e.g., localhost:3000 for React)
        // Use "*" for development, but specify exact domains in production
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        
        // 2. Allow HTTP methods
        corsConfig.setMaxAge(3600L); // Cache preflight response for 1 hour
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 3. Allow headers (especially Authorization for your JWT)
        corsConfig.addAllowedHeader("*");
        
        // 4. Expose headers if your UI needs to read custom headers from the response
        corsConfig.addExposedHeader("Authorization");

        // 5. Apply to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}