package com.shopsphere.api_gateway.filter;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtValidationFilter implements HandlerInterceptor{
    @Value("${jwt.secret}")
    private String secretKey;
    @Override
    public boolean preHandle(HttpServletRequest request , HttpServletResponse response , Object handler)throws Exception{
        if(request.getRequestURI().startsWith("/api/auth/")){
            return true;
        }
        String header = request.getHeader("Authorization");
        if(header == null  || !header.startsWith("Bearer ")){
            response.setStatus(401);
            return false;
        }
            String token = header.substring(7);
            if(!validateToken(token)){
                response.setStatus(401);
                return false;
            }
        return true;
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                .verifyWith(getSignkey())
                .build()
                .parseSignedClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    private SecretKey getSignkey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
