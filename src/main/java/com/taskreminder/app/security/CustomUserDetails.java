package com.taskreminder.app.security;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final String displayName;

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities, String name) {
        super(email, password, authorities);
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }
}