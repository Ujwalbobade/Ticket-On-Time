package com.example.JWTlogin.Config;

import com.example.JWTlogin.JWT.jwtauthenticationfilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationentrypoint;
    private final jwtauthenticationfilter jwtAuthenticationfilter;

    /*public SecurityConfig(AuthenticationEntryPoint authenticationentrypoint,jwtauthenticationfilter jwtauthenticationfilter2) {
        this.authenticationentrypoint = authenticationentrypoint;
        this.jwtAuthenticationfilter=jwtauthenticationfilter2;
    }*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
        System.out.println("security filterchain started");
        //disable cors
        httpSecurity.cors(Customizer.withDefaults());
        //disable Crsf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        //http request filter
        httpSecurity.authorizeHttpRequests(reqmatcher->
                reqmatcher.requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/Signup").permitAll()
                        .requestMatchers("/auth/ForgotPassword").permitAll().anyRequest().authenticated());

        //authentication entry point ->exception handling
        httpSecurity.exceptionHandling(ex->ex.authenticationEntryPoint(authenticationentrypoint));

        //session policy
        httpSecurity.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //jwt authentication filter
        httpSecurity.addFilterBefore(jwtAuthenticationfilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
