package com.example.SD.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder extends DaoAuthenticationProvider {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomPasswordEncoder(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        super();
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String presentedPassword = authentication.getCredentials().toString();
        String storedPassword = userDetails.getPassword();

        if (!passwordEncoder.matches(storedPassword, passwordEncoder.encode(presentedPassword))) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
