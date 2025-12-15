//package com.flightApp.filter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//
//import com.flightApp.util.JwtUtil;
//
//@Component
//public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
//
//    @Autowired
//    private RouteValidator validator;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public AuthenticationFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange, chain) -> {
//        	ServerHttpRequest request = exchange.getRequest();
//            
//            System.out.println("GATEWAY HIT: " + request.getURI().getPath());
//            
//            if (validator.isSecured.test(exchange.getRequest())) {
//                
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    throw new RuntimeException("Missing Authorization Header");
//                }
//
//                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
//                if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                    authHeader = authHeader.substring(7);
//                }
//
//                try {
//                    jwtUtil.validateToken(authHeader);
//
//                    String role = jwtUtil.extractClaim(authHeader, claims -> (String) claims.get("role"));
//
//                    String path = exchange.getRequest().getURI().getPath();
//                    
//                    System.out.println("DEBUG: Path requested: " + path);
//                    System.out.println("DEBUG: User Role found: " + role);
//                    
//                    if (path.contains("/flight/airline/inventory") && !role.equals("ROLE_ADMIN")) {
//                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                        return exchange.getResponse().setComplete();
//                    }
//
//
//                } catch (Exception e) {
//                    System.out.println("Invalid Access: " + e.getMessage());
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                }
//            }
//            return chain.filter(exchange);
//        });
//    }
//
//    public static class Config {}
//}

package com.flightApp.filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.flightApp.util.JwtUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie; // Import this
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // 1. Check if the request requires security
            if (validator.isSecured.test(exchange.getRequest())) {
                
                String token = null;

                // --- STRATEGY A: Try fetching from HEADER (Bearer Token) ---
                if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    }
                }

                // --- STRATEGY B: Try fetching from COOKIE (Browser Flow) ---
                // If header failed, look for the "JWT_TOKEN" cookie
                if (token == null) {
                    HttpCookie cookie = exchange.getRequest().getCookies().getFirst("JWT_TOKEN");
                    if (cookie != null) {
                        token = cookie.getValue();
                    }
                }

                // --- FINAL CHECK: Did we find a token? ---
                if (token == null) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED); // 401 Missing Token
                }

                try {
                    // 2. Validate the found token
                    jwtUtil.validateToken(token);

                    // 3. Extract Role for Authorization Checks
                    String finalToken = token; // Needed for lambda
                    String role = jwtUtil.extractClaim(finalToken, claims -> (String) claims.get("role"));
                    String path = exchange.getRequest().getURI().getPath();

                    // Rule: Only Admins can access inventory
                    if (path.contains("/flight/airline/inventory") && !"ROLE_ADMIN".equals(role)) {
                        return onError(exchange, HttpStatus.FORBIDDEN); // 403 Forbidden
                    }

                } catch (Exception e) {
                    System.out.println("Gateway Error: " + e.getMessage());
                    return onError(exchange, HttpStatus.UNAUTHORIZED); // 401 Invalid Token
                }
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {}
}