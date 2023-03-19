package com.spring_boot_sec_app.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtBody {
    private long tokenCreationTime;
    private String username;
    private String authorities;


//    public JwtBody(String username, String authorities) {
//        this.username = username;
//        this.authorities = authorities;
//        this.tokenCreationTime = System.currentTimeMillis();
//
//    }

}

