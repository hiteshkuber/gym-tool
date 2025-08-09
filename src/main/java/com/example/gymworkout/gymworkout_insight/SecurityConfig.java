package com.example.gymworkout.gymworkout_insight;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .csrf().disable();
        return http.build();
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService users() {
        var user = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("{noop}1234") // no encryption for now
                .roles("USER")
                .build();
        return new org.springframework.security.provisioning.InMemoryUserDetailsManager(user);
    }
}
