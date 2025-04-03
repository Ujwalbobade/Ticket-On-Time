package com.example.JWTlogin.Service;

import com.example.JWTlogin.JWT.JWTutil;
import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Model.Role;
import com.example.JWTlogin.Repository.UserRepository;
import com.example.JWTlogin.Service.UserDetails.AuthenticatManagerimpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service@RequiredArgsConstructor@Slf4j
public class AuthrizationService implements Authservice {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticatManagerimpl authenticationManager;
    @Autowired
    private EmailService emailService;


    @Override
    public String Login(String email, String Password) {
        // Create an authentication token with the provided credentials
        var authtoken =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, Password));
        log.info("Authtoken "+authtoken);
        SecurityContextHolder.getContext().setAuthentication(authtoken);
        log.info("passing token to authenticationManager");
        // Authenticate the token using the authentication manager
       // Authentication authenticate = authenticationManager.authenticate(authtoken);
        log.info("manager found the authenticate user in context provider");
        // If authentication is successful, generate a JWT token
        AppUser a = new AppUser();
        a.setEmail(authtoken.getPrincipal().toString());
        return JWTutil.generateToken(a.getUsername());

    }

    @Override
   public String SignUp(String Email, String Password, String Name, Role role) {
        //check weather user present in Db
        if(userRepository.existsByEmail(Email)){
            throw new RuntimeException("user already is present");
        }
        log.info("user not found in database");

        //encode the password
        var encodedpassowrd=passwordEncoder.encode(Password);
        log.info("role is"+role);
        //setting roles
        var authorites=new ArrayList<GrantedAuthority>();
        if(role.equals("USER")){
            authorites.add(new SimpleGrantedAuthority("USER"));
        }else{
            authorites.add(new SimpleGrantedAuthority("ADMIN"));
        }


        //create user object
        var user= AppUser.builder().Passwordd(encodedpassowrd).Name(Name).email(Email).AppUserRole(role).build();

        //save user
        userRepository.save(user);

        //generateToken
        var token=JWTutil.generateToken(Email);


        return token;
    }
    /*
    public boolean sendPasswordResetLink(Long id, String email) {
        Optional<AppUser> userOptional = userRepository.findByEmailAndId(email, id);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();

            // Generate a secure, time-limited password reset token
            String resetToken = JWTutil.generateToken(email);

            // Construct the password reset URL
            String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;

            // Send the reset link to the user's email
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

            return true;
        }

        return false;
    }*/
}
