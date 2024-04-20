package org.example.mahjong.config;

import org.example.mahjong.service.PlayerDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final PlayerDetailsService playerDetailsService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(PlayerDetailsService playerDetailsService, PasswordEncoder passwordEncoder) {
        this.playerDetailsService = playerDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authz -> authz
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll());
        return http.build();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(playerDetailsService).passwordEncoder(passwordEncoder);
    }
}