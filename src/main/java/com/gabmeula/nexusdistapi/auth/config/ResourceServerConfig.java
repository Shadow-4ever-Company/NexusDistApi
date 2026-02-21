package com.gabmeula.nexusdistapi.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfig {

        /**
         * Chain da API (Resource Server): valida JWT e protege os endpoints.
         * Aceita tanto JWT do login normal (POST /auth/login) quanto JWT do fluxo OAuth2
         * (App Client em POST /oauth2/token). POST /users e /users/ ficam em outra chain (registro).
         */
        @Bean
        @Order(3)
        SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(
                                                session -> session
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .securityMatcher("/products/**", "/orders/**", "/inventory/**",
                                                "/payments/**", "/users/me/**", "/")
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/users/me/**").authenticated()
                                                .requestMatchers("/products/**").hasAuthority("SCOPE_read")
                                                .requestMatchers("/orders/**").hasAuthority("SCOPE_read")
                                                .requestMatchers("/inventory/**").hasAuthority("SCOPE_read")
                                                .requestMatchers("/payments/**").hasAuthority("SCOPE_read")
                                                .requestMatchers("/").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                                }));

                return http.build();
        }

        @Bean
        @Order(2)
        SecurityFilterChain authLoginSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/auth/**")
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                                .anyRequest().denyAll());
                return http.build();
        }

        @Bean
        @Order(2)
        SecurityFilterChain usersSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/users", "/users/")
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.POST, "/users", "/users/").permitAll()
                                                .anyRequest().denyAll());
                return http.build();
        }
}