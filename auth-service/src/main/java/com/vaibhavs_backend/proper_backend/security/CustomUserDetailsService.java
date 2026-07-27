package com.vaibhavs_backend.proper_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vaibhavs_backend.proper_backend.entity.User;
import com.vaibhavs_backend.proper_backend.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userrepository;
    @Override
    public UserDetails loadUserByUsername(String username){
        User user  = userrepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    
    
}
