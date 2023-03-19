package com.spring_boot_sec_app.dto;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.spring_boot_sec_app.model.User;
import lombok.Data;

@Data
public class UpdateUser {
    private String name;
    private User user;
    public UpdateUser(String name, User user){
        name = user.getName();
    }
}
