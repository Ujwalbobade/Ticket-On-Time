package com.example.JWTlogin.Service.UserDetails;

import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticatManagerimpl implements AuthenticationManager {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticatManagerimpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // Extract credentials from the Authentication object
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();
    log.info("username:"+username+"password"+password);

        try {
            // Retrieve user details from the database
            AppUser user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Perform authentication logic (e.g., check against database)
            // For demonstration purposes, let's assume username and password are valid
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Create a new Authentication object with the authenticated user details
                return new UsernamePasswordAuthenticationToken(username, password);
            } else {
                // If authentication fails, throw an AuthenticationException
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (Exception e) {
            // Catch any exception while fetching data from the database
            log.error("Error while fetching user details: " + e.getMessage());
            throw new BadCredentialsException("Error while fetching user details", e);
        }
    }
}
