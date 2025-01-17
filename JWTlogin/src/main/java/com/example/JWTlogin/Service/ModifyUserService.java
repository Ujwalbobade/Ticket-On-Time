package com.example.JWTlogin.Service;

import com.example.JWTlogin.DTO.ModifyRequest;
import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Repository.UserRepository;
import com.example.JWTlogin.Service.UserDetails.AuthenticatManagerimpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service@RequiredArgsConstructor@Slf4j
public class ModifyUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticatManagerimpl authenticationManager;

    public String SavedUserinDB(ModifyRequest request){
        try{
        Optional<AppUser> optionalUser = userRepository.findByEmail(request.email());
        log.info("user with id "+request.email()+" is present"+optionalUser.isPresent());
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setEmail(request.email());
            user.setName(request.name());
            user.setAppUserRole(request.role());
            user.setModifyAt(String.valueOf(new Date()));
            user.setPasswordd(request.password());
            user.setPhoneNo(request.PhoneNo());
            // Modify user information here as needed
            userRepository.save(user);}
        return "saved";}
        catch(Exception e){
            throw  new RuntimeException("Error saving user in ModidyUSersercvive");
            }
    }
}
