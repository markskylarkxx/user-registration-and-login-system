package com.spring_boot_sec_app.controller;

import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.dto.UserDTO;

import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.service.UpdateUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class UpdateUserInfoController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UpdateUserInfoService userInfoService;


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePassport
            (@PathVariable("id") Long id, @RequestPart("user") MultipartFile passport) throws IOException {
        return ResponseEntity.ok().body(userInfoService.updatePassport(id, passport));

    }
@PutMapping("/user/{username}")
    public  ResponseEntity<?> updateStatus(@PathVariable("username") String username){
        return  ResponseEntity.ok().body(userInfoService.updateStatus(username));
    }
    @PutMapping("/update/user/")
    public ResponseEntity<?> updateUserInFo(@RequestBody User user,
                                            @RequestPart("user")UserDTO userDTO,
                                            @RequestPart("file") MultipartFile file) throws IOException {
     return  ResponseEntity.ok().body(userInfoService.updateUserInfo(user, userDTO, file));
    }

    @PutMapping("/update")
    public  ResponseEntity<?> updateUser( @RequestPart("user") UserDTO userDTO,
                                         @RequestPart("file") MultipartFile file) throws IOException {
        return  ResponseEntity.ok().body(userInfoService.updateUser( userDTO, file));
    }



}