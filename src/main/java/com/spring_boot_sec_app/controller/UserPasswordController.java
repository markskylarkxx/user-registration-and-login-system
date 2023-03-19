package com.spring_boot_sec_app.controller;

import com.spring_boot_sec_app.dto.ResetPassword;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.service.EmailServiceImpl;
import com.spring_boot_sec_app.service.PasswordResetService;
import com.spring_boot_sec_app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserPasswordController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping(value = "/forgot")
    public String forgotPass(@RequestBody User user){

        return  passwordResetService.forgotPass(user);
    }



    @PutMapping(value = "/reset/{id}")
    public  String resetPassword(@RequestBody ResetPassword resetPassword, @PathVariable Long id ){
        return  passwordResetService.resetPassword(resetPassword, id);
    }





}