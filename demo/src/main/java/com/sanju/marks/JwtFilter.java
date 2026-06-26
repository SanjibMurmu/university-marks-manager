package com.sanju.marks;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Custom stateless security filter that acts as the primary gatekeeper for the API.
 * Intercepts incoming HTTP requests to extract and validate JSON Web Tokens (JWT) 
 * from the Authorization header. If a valid token is found, it establishes the 
 * user's authentication context within the Spring Security framework.
 *
 * @author Sanjib Murmu
 * @version 1.0
 */

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * The core filtering logic executed exactly once per incoming request.
     * Extracts the Bearer token, validates its cryptographic signature via JwtUtil, 
     * and sets the SecurityContext to allow access to protected endpoints.
     *
     * @param request The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param filterChain The chain of subsequent security filters to execute.
     * @throws ServletException If a servlet-specific error occurs during filtering.
     * @throws IOException If an I/O error occurs during the request lifecycle.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Removes "Bearer " to get the raw token
            
            try {
                // Read the token to find out who is making the request
                String rollNo = jwtUtil.extractRollNo(token);
                
                //If the token is valid, tell Spring Security this user is officially logged in
                if (rollNo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(rollNo, null, new ArrayList<>());
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                
            }
        }
        
        chain.doFilter(request, response);
    }
}
