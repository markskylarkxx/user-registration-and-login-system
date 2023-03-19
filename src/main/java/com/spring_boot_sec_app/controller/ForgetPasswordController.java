package com.spring_boot_sec_app.controller;

import com.spring_boot_sec_app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForgetPasswordController {
    @Autowired
    private UserServiceImpl userService;
    @PostMapping(value = "/forget-pass")
    public  String forgetPassword(@RequestParam("email") String email){
        String response = userService.forgetPassword(email);
        if (!response.startsWith("Invalid")){
            response = "http://localhost:9000/api/reset-password?token=" ;

        }
        return response;
    }
    @PutMapping("/reset-password")
    private  String resetPassword(@RequestParam("token") String token,
                                  @RequestParam("new_password") String newPassword
    ){
        return  userService.resetPassword(token, newPassword);
    }


}
