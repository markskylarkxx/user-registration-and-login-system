package com.spring_boot_sec_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private  String password;
    private String passport;
    private String username;

    public UserDTO(String name, String email,String username) {
        this.name = name;
        this.email = email;
        this.username = username;
    }
}
