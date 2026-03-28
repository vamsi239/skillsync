package com.lpu.authservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lpu.authservice.entity.User;
import com.lpu.authservice.repository.UserRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
        		.requestMatchers(
        		        "/v3/api-docs/**",
        		        "/swagger-ui/**",
        		        "/swagger-ui.html",
        		        "/webjars/**",
        		        "/swagger-resources/**"
        		    ).permitAll()
        	    .requestMatchers("/auth/register", "/auth/login", "/auth/user/**").permitAll()
        	    .requestMatchers("/admin/**").hasRole("ADMIN")
        	    .requestMatchers("/mentor/**").hasRole("MENTOR")
        	    .requestMatchers("/user/**").hasRole("USER")
        	    .anyRequest().authenticated()
        	)
        
//        .authorizeHttpRequests(auth -> auth
//        	    .requestMatchers("/auth/**").permitAll()
//        	    .anyRequest().permitAll()   // TEMP ONLY
//        	)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if (repo.findByEmail("admin@gmail.com").isEmpty()) {

                User admin = User.builder()
                        .email("admin@gmail.com")
                        .password(encoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .mentorApproved(true)
                        .build();

                repo.save(admin);

                System.out.println("Default Admin Created");
            }
        };
    }
}
