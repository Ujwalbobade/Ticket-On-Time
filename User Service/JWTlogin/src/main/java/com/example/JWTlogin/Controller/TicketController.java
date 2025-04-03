package com.example.JWTlogin.Controller;

import com.example.JWTlogin.DTO.AllTicketResponse;
import com.example.JWTlogin.DTO.TicketRequest;
import com.example.JWTlogin.JWT.JWTutil;
import com.example.JWTlogin.Model.Ticket;
import com.example.JWTlogin.Service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class TicketController {
    @Autowired
    private  final TicketService ticketService;
    @Autowired
    private final JWTutil jwTutil;


    @PostMapping("/Users/PublishTicket")
    public ResponseEntity<String> publishTicket(@RequestBody TicketRequest request,@RequestHeader("Authorization") String token){
        log.info("Ticket contoller start"+token);
        // Validate the token here
        var tokenfilter=token.substring(7);
        if (jwTutil.validateTokenReceivedFromClient(tokenfilter)) {
            var username=jwTutil.getusernameformtoken(tokenfilter);
            // Token is valid, proceed with fetching user data
            log.info("User "+username+"is publishing ticket");
            ticketService.savedTicket(request, username.get());
            return ResponseEntity.ok("Ticket Creaeted successfully");
        }else{
            return ResponseEntity.status(404).body("Token is Expired");
        }

    }

    @GetMapping("/User/GetAllTicket")
    public ResponseEntity<AllTicketResponse> GetallTicekt(){
        var c=ticketService.getallticket();
        return  ResponseEntity.ok(AllTicketResponse.builder().allticket(c).build());
    }
}
