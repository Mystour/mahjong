package org.example.mahjong.config;

import org.example.mahjong.service.PlayerDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Web security configuration.
 * This class configures the security filter chain for the application.
 * The security filter chain is responsible for authenticating users and authorizing requests. （for login and register）
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {

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
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        // Set the default success login page to the welcome page
                        .defaultSuccessUrl("/welcome", true))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll());
        return http.build();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(playerDetailsService).passwordEncoder(passwordEncoder);
    }
}