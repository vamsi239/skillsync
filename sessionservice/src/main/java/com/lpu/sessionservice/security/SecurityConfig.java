package com.lpu.sessionservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter filter;

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            		
            		.requestMatchers(
            			    "/v3/api-docs/**",
            			    "/swagger-ui/**",
            			    "/swagger-ui.html"
            			).permitAll()

                // PUBLIC CHECK (for Feign)
                .requestMatchers("/session/check").permitAll()

                // USER actions
                .requestMatchers("/session").hasRole("USER")

                // MENTOR actions
                .requestMatchers("/session/*/complete").hasRole("MENTOR")
                .requestMatchers("/session/*/status").hasRole("MENTOR")

                // fallback
                .anyRequest().authenticated()
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
