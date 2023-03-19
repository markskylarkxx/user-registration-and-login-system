package com.spring_boot_sec_app.controller;

import com.spring_boot_sec_app.dto.LoginRequest;
import com.spring_boot_sec_app.dto.ResponseMessage;
import com.spring_boot_sec_app.dto.SignUpRequest;
import com.spring_boot_sec_app.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestPart("user") SignUpRequest signUpRequest,
                                          @RequestPart("file") MultipartFile file)  {
        try {

            return ResponseEntity.ok(authenticationService.registerUser(signUpRequest, file));
        }catch (Exception ex){

            ex.getMessage();
        }
        return  ResponseEntity.badRequest().body(new ResponseMessage("Bad request"));
    }
}
