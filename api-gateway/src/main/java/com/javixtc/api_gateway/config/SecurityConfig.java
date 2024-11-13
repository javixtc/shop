package com.javixtc.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                    .pathMatchers("/actuator/**").permitAll() // Permitir acceso a todas las rutas actuator
                    .anyExchange().authenticated()            // Requiere autenticación para otras rutas
                )
                .oauth2Login(Customizer.withDefaults())        // Configuración de login OAuth2
                .build();
    }
}
