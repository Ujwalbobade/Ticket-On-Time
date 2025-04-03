package com.example.JWTlogin.Service.UserDetails;

import com.example.JWTlogin.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         var appuser= userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found"));

        return new User(appuser.getUsername(),appuser.getPassword(),appuser.getAuthorities());

    }
}
