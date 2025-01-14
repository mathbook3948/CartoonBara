package com.jjangtrio.project1_back.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserForLogin implements UserDetails {

    public Long userNum;
    private String username; 
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return  this.username;
    }

}
