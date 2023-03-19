package com.spring_boot_sec_app.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Deactivated {
    @NotNull
    private  String deactivationReason;
}
