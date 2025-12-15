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
            "/eureka","/flight/search","flight/all"
    );

//    public Predicate<ServerHttpRequest> isSecured =
//            request -> openApiEndpoints
//                    .stream()
//                    .noneMatch(uri -> request.getURI().getPath().contains(uri))
//                    && !request.getURI().getPath().matches("/flight/[a-fA-F0-9]+");;
                    
                    
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri))
                    
                    // 1. YOUR REGEX FIX: Allow access to /flight/{id} (MongoID)
                    && !request.getURI().getPath().matches("/flight/[a-fA-F0-9]+") 
                    
                    // 2. CORS FIX: Allow OPTIONS requests for everything
                    && !request.getMethod().equals(HttpMethod.OPTIONS);
}
