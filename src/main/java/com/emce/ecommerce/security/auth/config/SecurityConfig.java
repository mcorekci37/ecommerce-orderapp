package com.emce.ecommerce.security.auth.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.emce.ecommerce.security.auth.service.LogoutService;
import com.emce.ecommerce.security.auth.service.CustomUserDetailsService;
import com.emce.ecommerce.security.customer.domain.valueobjects.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String[] WHITE_LIST_URL = {
          "/api/v1/auth/**",
          "/api/v1/public/**",
          "/v3/api-docs/**",
          "/v2/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/api-docs/**"
  };
  public static final String[] ADMIN_PATHS = {"/api/v1/order/ship/**", "/actuator/refresh"};
  public static final String LOGUT_PATH = "/api/v1/auth/logout";

  private final CustomUserDetailsService customUserDetailsService;
  private final LogoutService logoutService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(WHITE_LIST_URL)
                    .permitAll()
                    .requestMatchers(ADMIN_PATHS)
                    .hasAuthority(Role.ADMIN.toString()) // Restrict access to matchOrder endpoint
                    .anyRequest()
                    .authenticated())
        .headers(
            (headers) ->
                headers.frameOptions(
                    (frameOptions) ->
                        frameOptions.disable()) // Disable frame options for H2 console
            )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl(LOGUT_PATH)
                    .addLogoutHandler(logoutService)
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          SecurityContextHolder.clearContext();
                          response.setStatus(HttpServletResponse.SC_OK);
                        }));
    ;
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
