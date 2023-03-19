package com.spring_boot_sec_app.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ResetPassword {
    @NotNull
    private String token;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;
    // private String user_id;

}