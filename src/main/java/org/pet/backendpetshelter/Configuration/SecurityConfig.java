package org.pet.backendpetshelter.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth endpoints
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()

                        // animal endpoint
                        .requestMatchers("/api/animal").permitAll()
                        .requestMatchers("/api/species").permitAll()
                        .requestMatchers("/api/breed").permitAll()
                        .requestMatchers("/api/veterinarian").permitAll()
                        .requestMatchers("/api/").permitAll()
                        .requestMatchers("/api/animal").permitAll()
                        
                        // Public docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // Example domain routes (adjust to your API)
                        .requestMatchers(HttpMethod.GET, "/api/user/**")
                        .hasAnyAuthority("ADMIN", "STAFF", "USER", "VETERINARIAN", "ADOPTER", "FOSTER")
                        // ... flere matchers efter dit behov ...

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}