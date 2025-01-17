package com.example.JWTlogin.Controller;

import com.example.JWTlogin.JWT.JWTutil;
import com.example.JWTlogin.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    @Autowired
    private final UserRepository userRepository;

  /*  @DeleteMapping("/Users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,@RequestHeader("Authorization") String token){
        String t="";
        try{
            var tokenfilter=token.substring(7);
            if (JWTutil.validateTokenReceivedFromClient(tokenfilter)) {
                    userRepository.deleteById(id);
        t= new StringBuilder().append("USER with ID :-").append(id).toString();}}
        catch (Exception e) {
            throw  new RuntimeException("user id is not getting fetch from database check Delete controller once");
        }
        return ResponseEntity.ok(t);
    }*/


}


