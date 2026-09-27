package com.anax.account.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    MapReactiveUserDetailsService userDetailsService(
            @Value("${account.security.username}") String username,
            @Value("${account.security.password}") String password) {
        if (password.isBlank()) {
            throw new IllegalStateException("ACCOUNT_API_PASSWORD must be configured");
        }
        String encodedPassword = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
        return new MapReactiveUserDetailsService(User.withUsername(username)
                .password(encodedPassword)
                .roles("USER")
                .build());
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}