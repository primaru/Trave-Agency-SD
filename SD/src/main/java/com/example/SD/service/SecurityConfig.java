package com.example.SD.service;

import com.example.SD.model.Client;
import com.example.SD.model.Favorite;
import com.example.SD.repository.ClientRepository;
import com.example.SD.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CustomPasswordEncoder customPasswordEncoder = new CustomPasswordEncoder(userDetailsService,passwordEncoder);
        http.authenticationProvider(customPasswordEncoder);
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/", "/home", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                HttpSession session = request.getSession();
                                if (session != null) {
                                    String username = authentication.getName();
                                    session.setAttribute("loggedInUser", username);
                                    Client client = clientRepository.findByUsername(username);
                                    Set<Favorite> favorites = new HashSet<>(favoriteRepository.findByClientId(client.getId()));
                                    session.setAttribute("favorites", favorites);
                                }
                                response.sendRedirect("/dashboard"); // Redirect to the dashboard

                            }
                        })
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                String errorMessage = "Invalid username or password.";
                                if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
                                    errorMessage = "Your account has been disabled.";
                                } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
                                    errorMessage = "Your account has expired.";
                                }
                                request.getSession().setAttribute("loginError", errorMessage);
                                response.sendRedirect("/login");
                            }
                        })
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .authenticationProvider(customAuthenticationProvider());

        return http.build();
    }

    @Bean
    public CustomPasswordEncoder customAuthenticationProvider() {
        return new CustomPasswordEncoder(userDetailsService, passwordEncoder);
    }
}
