package com.flightApp.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/token",
            "/auth/validate",
            "/eureka","/flight/search","flight/all",
            "/auth/forgot-password",
            "/auth/reset-password"
    );
                   
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri))
                    
                    && !request.getURI().getPath().matches("/flight/[a-fA-F0-9]+") 
                    
                    && !request.getMethod().equals(HttpMethod.OPTIONS);
}
