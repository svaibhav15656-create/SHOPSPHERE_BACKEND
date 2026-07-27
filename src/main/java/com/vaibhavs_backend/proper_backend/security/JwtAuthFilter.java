package com.vaibhavs_backend.proper_backend.security;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    @Autowired
    private JwtService jwtservice;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    System.out.println("AUTH HEADER: " + authHeader);
    if(authHeader == null ||  !authHeader.startsWith("Bearer ")){
        filterChain.doFilter(request,response);
        return;
    }
    String token = authHeader.substring(7);
    String email = jwtservice.extractEmail(token);
    System.out.println("EXTRACTED EMAIL: " + email);
    if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
        UserDetails userdetails = customUserDetailsService.loadUserByUsername(email);
        System.out.println("LOADED USER AUTHORITIES: " + userdetails.getAuthorities());
        if(jwtservice.isTokenValid(userdetails, token)){
            System.out.println("TOKEN IS VALID - SETTING AUTHENTICATION");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userdetails,null,userdetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            System.out.println("TOKEN IS INVALID");
        }
    }
    filterChain.doFilter(request, response);
}
}
    
    

