package com.danskebank.dummy.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtTokenProvider {
@Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-jwtExpirationDate}")
    private String jwtExpirationDate;


    public String generateToken(Authentication authentication){
        String userName= authentication.getName();
      /*  Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() +jwtExpirationDate);*/

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expireDateTime = currentDateTime.plusSeconds(Long.parseLong(jwtExpirationDate));

        Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expireDate = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ;
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();

    }

    private Key key(){
    byte[] bytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(bytes);
    }

    public String getUserName(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (ExpiredJwtException | IllegalArgumentException | SignatureException | MalformedJwtException e) {
            throw new RuntimeException(e);
        }
    }
}
