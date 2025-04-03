package com.example.JWTlogin.Controller;

import com.example.JWTlogin.Config.AuthenticationConfig;
import com.example.JWTlogin.DTO.*;
import com.example.JWTlogin.JWT.JWTutil;
import com.example.JWTlogin.Model.AppUser;
import com.example.JWTlogin.Repository.UserRepository;
import com.example.JWTlogin.Service.AuthrizationService;
import com.example.JWTlogin.Service.EmailService;
import com.example.JWTlogin.Service.ModifyUserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class AuthenticationController {
    @Autowired
    private final AuthrizationService authrizationService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ModifyUserService modifyUserService;
    @Autowired
    private  final PasswordEncoder passwordEncoder;
    @Autowired
    private final EmailService emailService;


/*    @PostMapping("/Signup")
    public ResponseEntity<AuthResponse> Signup(@RequestBody SignupRequest request) {
        log.info("Received signup request: {}", request);

        var token = authrizationService.SignUp(request.email(), request.password(), request.name(), request.role());
        var response = new AuthResponse(token, AuthStatus.USER_SUCCESSFULLY_CREATED);

        log.info("Signup successful for user: {}", request.email());
        return ResponseEntity.ok(response);
    }*/
@CircuitBreaker(name = "authService", fallbackMethod = "fallbackMethod")
@PostMapping("/Signup")
public ResponseEntity<AuthResponse> Signup(@RequestBody SignupRequest request) {
    log.info("Received signup request: {}", request);
    var token = authrizationService.SignUp(request.email(), request.password(), request.name(), request.role());
    var response = new AuthResponse(token, AuthStatus.USER_SUCCESSFULLY_CREATED);
    log.info("Signup successful for user: {}", request.email());
    return ResponseEntity.ok(response);
}
    //you can remove response httplservlet
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        log.info("Received login request: {}", request);

        var jwtToken = authrizationService.Login(request.email(), request.password());
        // Set JWT token in a cookie
        Cookie cookie = new Cookie("jwtToken", jwtToken);
       cookie.setMaxAge(3600); // Set cookie expiry time in seconds (e.g., 1 hour)
        cookie.setPath("/"); // Set cookie path to root ("/") for all endpoints
       response.addCookie(cookie);
        Optional<AppUser> user=userRepository.findByEmail(request.email());

        var responsee = new LoginResponse(jwtToken, AuthStatus.LOGIN_SUCCESS,user.get().getAppUserRole(),user.get().getName(),user.get().getId());

        log.info("Login successful for user: {}", request.email());
        return ResponseEntity.ok(responsee);
    }
   /* @GetMapping("/Users")
    public ResponseEntity<AlluserResponse> Demo(){
        var b= new AlluserResponse(userRepository.findAll());
        return ResponseEntity.ok(b);
    }*/

    @Autowired
    private final JWTutil jwTutil;

    @GetMapping("/Users")
    public ResponseEntity<AlluserResponse> getUsers(@RequestHeader("Authorization") String token) {
        log.info("demo contoller start"+token);
        // Validate the token here
        var tokenfilter=token.substring(7);
        if (jwTutil.validateTokenReceivedFromClient(tokenfilter)) {
            // Token is valid, proceed with fetching user data
            return ResponseEntity.ok(AlluserResponse.builder().alluser(userRepository.findAll()).build());
        } else {
            // Token is invalid, return unauthorized response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //return ResponseEntity.ok(AlluserResponse.builder().alluser(userRepository.findAll()).build());
    }

    @DeleteMapping("/Users/{id}")
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
    }

    @GetMapping("Users/{id}")
    public ResponseEntity<AppUser> GetUserbyId(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            var filteredToken = token.substring(7);
            if (JWTutil.validateTokenReceivedFromClient(filteredToken)) {
                log.info("checking user with id "+id);
                Optional<AppUser> optionalUser = userRepository.findById(id);
                if (optionalUser.isPresent()) {
                    AppUser user = optionalUser.get();
                    // Modify user information here as needed
                    // Save the modified user information
                    return ResponseEntity.ok(user); // Return the modified user information
                } else {
                    return ResponseEntity.notFound().build(); // User not found
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid token
            }
        } catch (Exception e) {
            throw new RuntimeException("Error modifying user: " + e.getMessage());
        }
    }
    @PostMapping("Users/Modify/{id}")
    public ResponseEntity<String> ModifyUser(@RequestHeader("Authorization") String token,@RequestBody ModifyRequest request) {
        String s="";
        try {
            var filteredToken = token.substring(7);
            if (JWTutil.validateTokenReceivedFromClient(filteredToken)) {
                    modifyUserService.SavedUserinDB(request);
                    s="Modified sucessfully";
                    return ResponseEntity.ok(s); // Return the modified user information
                } else {
                    return ResponseEntity.notFound().build();
    }
} catch (Exception e) {
            throw new RuntimeException("Error modifying user: " + e.getMessage());
        }
    }
    @GetMapping("Users/Modify/{id}/verifyPassword")
    public ResponseEntity<String> verifyPassword(@PathVariable Long id,@RequestBody String password, @RequestHeader("Authorization") String token) {
        try {
            log.info("Password verification started");
            String filteredToken = token.substring(7); // Remove "Bearer " prefix from the token
            if (JWTutil.validateTokenReceivedFromClient(filteredToken)) {


                // Retrieve the user's password from the database based on userId (Assuming you have a UserRepository)
                String savedPassword = userRepository.findById(id).map(AppUser::getPasswordd).orElse(null);
                log.info("passowrd retrive from DB"+savedPassword);
                log.info("password match "+ passwordEncoder.matches(password,savedPassword));
                //var encrptypass=encoder.encode(password);
                if (savedPassword != null && passwordEncoder.matches(password,savedPassword)) {
                    return ResponseEntity.ok("Password is correct");
                } else {
                    return ResponseEntity.badRequest().body("Incorrect password");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid token
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying password");
        }
    }

    @PostMapping("/Forgetpassword")
    public ResponseEntity<String> forgotPassword(@RequestBody String email, @RequestHeader("Authorization") String token) {
        try {
            // Validate token (assuming you use JWT)
            String filteredToken = token.substring(7);  // Remove 'Bearer ' prefix
            if (!JWTutil.validateTokenrecivedfromCilent(filteredToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            // Process the password reset (send email logic)
            boolean linkSent = emailService.sendPasswordResetEmail(email);

            if (linkSent) {
                return ResponseEntity.ok("Password reset link sent successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or email mismatch.");
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        // Validate the token
        if (!JWTutil.validateTokenReceivedFromClient(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // Extract the user information from the token
        String email = JWTutil.extractEmailFromToken(token);

        // Retrieve the user from the database
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Get the user and update the password
        AppUser user = userOptional.get();
        user.setPasswordd(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password successfully reset");
    }
    public ResponseEntity<AuthResponse> fallbackMethod(SignupRequest request, Throwable t) {
        log.error("Signup failed for user: {}", request.email(), t);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(null, AuthStatus.LOGIN_FAILED));
    }


}