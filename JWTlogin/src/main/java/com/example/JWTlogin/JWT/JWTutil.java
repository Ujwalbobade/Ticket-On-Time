package com.example.JWTlogin.JWT;



import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class JWTutil {
    private static final String ISSUER = "UJ07 Sever";

    private JWTutil(){}
    private static final KeyPair keys = Keys.keyPairFor(SignatureAlgorithm.RS512);
    private static SecretKey  secretKey22= Jwts.SIG.HS384.key().build();
    private static final String SECRET_KEY_STRING = "474004a3c699e8182499f98e3870e6375553d8b5fb42e0e6beff100c75422639";
    private static final byte[] SECRET_KEY_BYTES = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
    private static final SecretKey secretKey = new SecretKeySpec(SECRET_KEY_BYTES, SignatureAlgorithm.HS384.getJcaName());



    private static String  secretKey11="474004a3c699e8182499f98e3870e6375553d8b5fb42e0e6beff100c75422639";
    //byte[] secret = stringSecret.getBytes();


    public static boolean validatetoken(String jwttoken) {
        log.info("checking token is valid"+jwttoken);
        return parseToken2(jwttoken).isPresent();
    }

    public  static  boolean validateTokenrecivedfromCilent(String token){
        try {
            // Check token expiration
            Optional<Date> expirationDate = getDate(token);
            if (expirationDate == null || expirationDate.get().before(new Date())) {
                log.error("Token has expired.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build().hasBody();
            } else {
                return true;
            }
        }catch (Exception e ) {
            log.error("Error processing token: " +e.getMessage());

        }
        return false;
    }

    public static boolean validateTokenReceivedFromClient(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            if (expirationDate == null || expirationDate.before(new Date())) {
                log.error("Token has expired.");
                return false;
            } else {
                // Optionally, log additional token information for debugging
                log.debug("Token is valid. Subject: {}", claims.getSubject());
                return true;
            }
        } catch (ExpiredJwtException e) {
            log.error("Token has expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("JWT exception occurred: {}", e.getMessage());
            return false;
        }
    }



    private static Optional<Claims> parseToken(String jwttoken) {
        var jwtParser= Jwts.parser().verifyWith(secretKey).build();
        try{
            return Optional.of(jwtParser.parseSignedClaims(jwttoken).getPayload());
        }catch (JwtException e){
            log.error("JWT exception occur");
        }catch (IllegalArgumentException e){
            throw  new RuntimeException(e);
        }
        return  Optional.empty();
    }

    private static Optional<Claims> parseToken2(String jwttoken) {
        try {
            JwtParser jwtParser = Jwts.parser().setSigningKey(secretKey).build();
            return Optional.of(jwtParser.parseSignedClaims(jwttoken).getBody());
        } catch (JwtException e) {
            log.error("JWT exception occurred: {}", e.getMessage());
        }
        return Optional.empty();
    }




    public static boolean parseTokenforcilent(String jwttoken) {
        try {
            // Parse the token and extract its claims
            var ppp=Jwts.parser().decryptWith(getSigninKey()).build().parseClaimsJws(jwttoken).getPayload();
            try {
                // Check token expiration
                Optional<Date> expirationDate = Optional.ofNullable(ppp.getExpiration());
                if (expirationDate == null || expirationDate.get().before(new Date())) {
                    log.error("Token has expired.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build().hasBody();
                } else {
                    return true;
                }
            }catch (Exception e ) {
                log.error("Error processing token: " +e.getMessage());

            }
            return false;
        } catch (JwtException e) {         log.error("JWT exception occurred: {}", e.getMessage());     } catch (IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return false;

    }

    public static Optional<String> getusernameformtoken(String jwttoken) {
        var claims=parseToken(jwttoken);

        return claims.map(Claims::getSubject);

    }




    //cheking token is valid
    //if token username is equal to userdetails username then
    public static boolean isValid(String token, UserDetails user){
        String username=getusername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
    //check token is exipred or not
    private static boolean isTokenExpired(String token) {
        return expirationTime(token).before(new Date());
    }
    //get time from token
    private static Date expirationTime(String token) {
        return extractClaims(token,Claims::getExpiration);
    }

    //get username from token
    public static String getusername(String token){
        return extractClaims(token,Claims::getSubject);
    }



    //made generic funtion to get dynamic data from the token
    public static <T> T extractClaims(String token, Function<Claims, T> resolver){
        Claims claims=extractAllClaims(token);
        return resolver.apply(claims);
    }

    public static Optional<Date> getDate(String jwttoken){
        var claims=parseToken(jwttoken);
        return claims.map(c->c.getExpiration());
    }
    //all the property will be extracted from the method like issuer time username
    private static Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public static String generateToken(String email) {
        var currentDate=new Date();
        var tokenexp=24*60*60*1000;
        var expiration=new Date(currentDate.getTime()+ tokenexp);
        return Jwts.builder().id(UUID.randomUUID().toString()).issuer(ISSUER).subject(email).signWith(secretKey)
                .issuedAt(currentDate).expiration(expiration)
                .compact();
    }

    public static String generateToken1(String email) {
        var currentDate=new Date();
        var tokenexp=1000 * 60 * 60;
        var expiration=new Date(currentDate.getTime()+ tokenexp);
        return Jwts.builder().id(UUID.randomUUID().toString()).issuer(ISSUER).subject(email).signWith(secretKey)
                .issuedAt(currentDate).expiration(expiration)
                .compact();
    }

    private static SecretKey getSigninKey() {
        byte[] keybytes= Decoders.BASE64.decode(secretKey11);
        return Keys.hmacShaKeyFor(keybytes);
    }

    public static String extractEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
}
