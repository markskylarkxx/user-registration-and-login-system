package com.spring_boot_sec_app.mapper;

import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.dto.UserDTO;
import lombok.Data;

@Data


public class UserMapper {

     //convert jpa entity int userdto entity return userdto;
    public  static UserDTO mapToUserDto(User user){
        UserDTO userDTO = new UserDTO(user.getName(), user.getEmail(), user.getUsername());
        return  userDTO;
    }

    // convert userdeto in user entity and return user object;

    public static User mapToUser(UserDTO userDTO){
        User user = new User(userDTO.getName(), userDTO.getUsername(), userDTO.getEmail() );
        return  user;
    }
}
