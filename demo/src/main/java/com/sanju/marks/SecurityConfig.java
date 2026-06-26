package com.sanju.marks; 

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Core security configuration class for the application.
 * Defines the stateless, JWT-based security architecture, configures cross-origin 
 * resource sharing (CORS), and dictates precise role-based access control (RBAC) 
 * routing rules for all API endpoints.
 *
 * @author Sanjib Murmu
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    
    /**
     * Provisions the BCrypt password encoder bean.
     * Ensures all passwords are securely hashed with a cryptographic salt before 
     * persistence, protecting against database breach vulnerabilities.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //The Traffic Controller
    /**
     * Configures the primary security filter chain.
     * Disables default CSRF protection (as stateless tokens are used), defines public 
     * endpoints (e.g., login, password recovery, preflight checks), locks down secure 
     * endpoints, and injects the custom JwtFilter into the execution pipeline.
     *
     * @param http The HttpSecurity builder.
     * @return The finalized SecurityFilterChain.
     * @throws Exception If a configuration error occurs during initialization.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            }))
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/students/add").permitAll() 
                .requestMatchers("/api/students/login").permitAll() 
                .requestMatchers("/api/students/forgot-password").permitAll()
                .requestMatchers("/api/students/reset-password").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Locking down everything else
                .anyRequest().authenticated()
            )
            // Putting the Bouncer at the front door
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(basic -> basic.disable()); 

        return http.build();
    }
}