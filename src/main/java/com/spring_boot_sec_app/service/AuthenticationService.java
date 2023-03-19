package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.dto.JwtAuthenticationResponse;
import com.spring_boot_sec_app.dto.LoginRequest;
import com.spring_boot_sec_app.dto.ResponseMessage;
import com.spring_boot_sec_app.dto.SignUpRequest;
import com.spring_boot_sec_app.exception.AppException;
import com.spring_boot_sec_app.exception.ResourceNotFoundException;
import com.spring_boot_sec_app.model.Role;
import com.spring_boot_sec_app.model.RoleName;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.repo.RoleRepository;
import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.security.CustomAuthenticationSecurityProvider;
import com.spring_boot_sec_app.session.SessionStorage;
import com.spring_boot_sec_app.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service@Slf4j
public class AuthenticationService {
    @Autowired
    private CustomAuthenticationSecurityProvider customProvider;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtTokenUtils tokenUtils;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private SessionStorage sessionStorage;

    public JwtAuthenticationResponse authenticate(LoginRequest request) {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.isAuthenticated());


        Authentication authentication = customProvider
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));


        if (authentication.isAuthenticated()) {
            System.out.println(authentication.isAuthenticated());
            SecurityContextHolder.getContext().setAuthentication(authentication);


              //  generate the token
            String token = tokenUtils.generateToken(authentication);
            User user = (User) authentication.getCredentials();
            sessionStorage.startSession(user);
            return new JwtAuthenticationResponse(token);


        }

        return null;
    }
    public ResponseEntity<?> registerUser( SignUpRequest request, MultipartFile passport) throws  Exception{

        String passportUpload = FileUploadService.uploadFile(passport);

        if (userRepo.existsByUsername(request.getUsername())){


            return ResponseEntity.badRequest().body(new ResponseMessage("Error: Username is already taken"));

        }


        if (userRepo.existsByEmail(request.getEmail())){

            return  ResponseEntity.badRequest().body(new ResponseMessage("Error: email is already taken"));
        }
        //  CREATE A NEW USER ACCOUNT



        User user = new User(request.getName(), request.getUsername(), request.getEmail(),
                encoder.encode(request.getPassword()), passportUpload);


        Set<String> strRoles = new HashSet<>();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null){
            Role userRole = roleRepo.findByName
                    (RoleName.ROLE_USER).orElseThrow(()->new RuntimeException("Error:Role is not found"));
            roles.add(userRole);
        }else {
            strRoles.forEach(role->{
                switch (role){
                    case "admin":
                        Role adminRole =
                                roleRepo.
                                        findByName(RoleName.ROLE_ADMIN).
                                        orElseThrow(()->new RuntimeException("Error:Role is not found"));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepo
                                .findByName(RoleName.ROLE_USER).
                                orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setActive(Boolean.TRUE);

        user.setRoles(roles);
        userRepo.save(user);
        return  ResponseEntity.ok(new ResponseMessage("User registered successfully"));
    }
}
