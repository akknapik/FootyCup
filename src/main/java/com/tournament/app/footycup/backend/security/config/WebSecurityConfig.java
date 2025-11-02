package com.tournament.app.footycup.backend.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.app.footycup.backend.requests.ErrorResponse;
import com.tournament.app.footycup.backend.security.filter.TokenAuthenticationFilter;
import com.tournament.app.footycup.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/tournaments/my", "/api/tournaments/my").authenticated()
                        .requestMatchers(
                                "/register", "/login", "/logout",
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/api/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs",
                                "/api/docs", "/api/docs/**",
                                "/webjars/**", "/swagger-resources/**", "/configuration/**",
                                "/favicon.ico",
                                "/api/auth/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/tournaments/public", "/tournaments/public/**",
                                "/api/tournaments/public", "/api/tournaments/public/**",
                                "/tournaments/*", "/tournaments/*/**",
                                "/api/tournaments/*", "/api/tournaments/*/**",
                                "/tournament/**", "/api/tournament/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (response.isCommitted()) return;
                            ErrorResponse error = new ErrorResponse(
                                    HttpStatus.UNAUTHORIZED.value(), "Unauthorized access", LocalDateTime.now()
                            );
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getOutputStream(), error);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (response.isCommitted()) return;
                            ErrorResponse error = new ErrorResponse(
                                    HttpStatus.FORBIDDEN.value(), "Access denied", LocalDateTime.now()
                            );
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getOutputStream(), error);
                        })
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.disable())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
