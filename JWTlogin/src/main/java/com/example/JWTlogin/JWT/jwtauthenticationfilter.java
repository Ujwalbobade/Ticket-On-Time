package com.example.JWTlogin.JWT;

import com.example.JWTlogin.Service.UserDetails.UserDetailsServiceImpl;
import com.mysql.cj.log.Log;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class jwtauthenticationfilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;

    private final JWTutil JWTservice;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //fetch token from request
        var jwttokenOptional=gettokenFromRequest(request);
        //log.info("request from http client"+jwttokenOptional.get());
        //log.info("token check in request"+jwttokenOptional.get());
//        var Authheader=request.getHeader("Authorization");
//
//        if(Authheader==null || !Authheader.startsWith("Bearer ")){
//            filterChain.doFilter(request,response);
//        }
//
//        String token=Authheader.substring(7);
//        String username=JWTservice.getusername(token);
//
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails=userDetailsService.loadUserByUsername(username);
//
//
//            //chedking token is valid
//            if(JWTservice.isValid(token,userDetails)){
//                UsernamePasswordAuthenticationToken authtoken= new UsernamePasswordAuthenticationToken(userDetails,null);
//
//                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authtoken);
//
//            }
//        }
//        filterChain.doFilter(request,response);

        //validate JWT token ->JWT utils
        jwttokenOptional.ifPresent(jwttoken->{
            try {
                if (JWTutil.validateTokenReceivedFromClient(jwttoken)) {
                    log.info("jwt token:-"+jwttoken);
                    log.info("enter "+JWTutil.validateTokenReceivedFromClient(jwttoken));
                    //fetch username form token
                    var usernameoptional = JWTutil.getusernameformtoken(jwttoken);
                    log.info("username:-"+usernameoptional.get());



                    usernameoptional.ifPresent(username -> { //fetch user details with help of username
                        var userDetails = userDetailsService.loadUserByUsername(usernameoptional.get());


                        //create authentication token
                        var authenticatontoken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                        authenticatontoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        //set authentication token to security context
                        SecurityContextHolder.getContext().setAuthentication(authenticatontoken);
                    });


                }
            }catch (Exception e) {
                // Log the exception
                logger.error("Error occurred while validating JWT token: {}"+e.getMessage());
                // Optionally, handle the exception or return an error response
            }
        });

        //pass request and respsone to next filter
        filterChain.doFilter(request,response);

    }

    private Optional<String> gettokenFromRequest(HttpServletRequest request) {
        //extract authenticatiom headert

        var  authHeader = request.getHeader("Authorization");
        //Bearer <JWT token>
        if(StringUtils.hasText(authHeader)&& authHeader.startsWith("Bearer ")){
            return Optional.of(authHeader.substring(7));}
        return Optional.empty();
    }
    }
