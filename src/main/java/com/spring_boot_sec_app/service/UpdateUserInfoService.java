package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.dto.ResponseMessage;
import com.spring_boot_sec_app.exception.AppException;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.dto.UserDTO;
import com.spring_boot_sec_app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UpdateUserInfoService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    //TODO; UPDATE THE PASSPORT!
    public ResponseEntity<ResponseMessage> updatePassport(Long id, MultipartFile passport) throws IOException {

        //check if the user exist first

        String updatePassport = FileUploadService.uploadFile(passport);
        String message = "";
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new AppException("No user found with id" + id);


        }
        try {
            User u = user.get();
            u.setPassport(updatePassport);
            userRepository.save(u);
            message = "Passport has been updated";
            return ResponseEntity.ok().body(new ResponseMessage(message));

        } catch (Exception ex) {
            ex.getMessage();
            message = "Passport was not updated";
            return ResponseEntity.badRequest().body(new ResponseMessage(message));

        }

    }
    //TODO; UPDATE  USER  ACTIVE STATUS;
    public ResponseEntity<ResponseMessage>  updateStatus(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()){
            User u = user.get();
            if (u.getActive() == Boolean.FALSE || u.getActive() == null){
                u.setActive(Boolean.TRUE);
            }
            userRepository.save(u);
        }
        return  ResponseEntity.ok().body(new ResponseMessage("status is updated"));

    }




    public Object updateUserInfo(User user, UserDTO dto, MultipartFile file) throws IOException {

        // check to see if the user is present
        Optional<User> isUser = userRepository.findByUsername(user.getUsername());
        if (!isUser.isPresent()){
            throw new AppException("User not found with the given id " + user.getId());
        }
        // else get the available user
        User u = isUser.get();

        userRepository.save(u);
        return  "User info has been updated";

    }

    public Object updateUser( UserDTO userDTO, MultipartFile passport) throws IOException {

        //check if the user is present in the database;
        Optional<User> isUser = userRepository.findByUsername(userDTO.getUsername());
        if (!isUser.isPresent()){
            throw  new UsernameNotFoundException("Username not found");
        }
        String passportUpdate = FileUploadService.uploadFile(passport);

           // Get the user object and update the user.
        User u = isUser.get();
        u.setName(userDTO.getName());
        u.setPassword(encoder.encode(userDTO.getPassword()));
        u.setEmail(userDTO.getEmail());
        u.setUsername(userDTO.getUsername());
        // update the passport;
       if (passport.isEmpty()){
           throw new AppException("Passport is empty");
       }
        u.setPassport(passportUpdate);
       return  userRepository.save(u);

    }
}
