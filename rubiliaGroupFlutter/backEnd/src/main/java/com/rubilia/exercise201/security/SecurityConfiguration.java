package com.rubilia.exercise201.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rubilia.exercise201.service.util.CustomerSecurityService;
import com.rubilia.exercise201.service.util.StaffAccountSecurityService;

import java.util.Arrays;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("customerAuthManager")
    public AuthenticationManager customerAuthManager(CustomerSecurityService service) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean("staffAuthManager")
    public AuthenticationManager staffAuthManager(StaffAccountSecurityService service) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // CẤU HÌNH QUAN TRỌNG: Cho phép cả domain thật và localhost
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", 
            "https://rubilia.store", 
            "http://rubilia.store"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, StaffAccountSecurityService staffDetailsService) throws Exception {
        OidcUserService googleUserService = new OidcUserService();
        googleUserService.setAccessibleScopes(Set.of("email", "profile", "openid"));

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF để tránh lỗi 403 khi Register/Login
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/customers/login/**", "/api/customers/register/**", "/api/customers/oauth2/**").permitAll()
                .requestMatchers("/api/staff/login/**").permitAll()
                .requestMatchers("/api/products/**", "/api/sales/**", "/api/uploads/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/customer/**").authenticated()
                .requestMatchers("/login/oauth2/code/**").permitAll()
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .httpBasic(Customizer.withDefaults())
            .userDetailsService(staffDetailsService)
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(googleUserService))
                .defaultSuccessUrl("/api/customers/oauth2/success", true)
                .failureUrl("/api/customers/oauth2/failure")
            );

        return http.build();
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}