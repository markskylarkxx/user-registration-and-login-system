package com.spring_boot_sec_app.security;

import com.spring_boot_sec_app.constants.SecurityDetails;
import com.spring_boot_sec_app.exception.AppException;
import com.spring_boot_sec_app.exception.ResourceNotFoundException;
import com.spring_boot_sec_app.model.LockedUser;
import com.spring_boot_sec_app.model.Role;
import com.spring_boot_sec_app.model.RoleName;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.repo.DisableUserRepo;
import com.spring_boot_sec_app.repo.LockedUserRepository;
import com.spring_boot_sec_app.repo.RoleRepository;
import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CustomAuthenticationSecurityProvider implements AuthenticationProvider {

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private AuthenticationManager authenticationManager;

    public CustomAuthenticationSecurityProvider() {
        super();
    }

    private static int loginCount = 0;

    private boolean isValidCredential = true;
    @Autowired
    private DisableUserRepo disableUserRepo;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LockedUserRepository lockedUserRepository;
    @Autowired
    RoleRepository roleRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        String username = (String) authentication.getPrincipal(); // username
        String password = authentication.getCredentials().toString(); // password


        UsernamePasswordAuthenticationToken authenticationToken;


        //GET THE USER FROM THE DATABASE

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found!"));

        Optional<Role> isUser = user.getRoles().stream().
                filter(f -> f.getName() == RoleName.ROLE_USER).findAny();

        if (isUser.isPresent()) {


            // check if the user is locked out of the system
            Optional<LockedUser> lockedUser = this.getUserById(user);
            if (lockedUser.isPresent()) {
                throw new AppException("User is locked!");
            }

            //check if the user is deactivated
            if (user.getActive()) {

                // Check the two passwords to see if the match. if they do not match throw an error message.
                if (!this.checkPassword(user, password)) {
                    this.handleFailedLogin(user);

                    throw new AppException("Username or password is incorrect");
                }


                // Get the user details object of the currently logged-in user
                UserPrincipal userDetails = UserPrincipal.create(user);

                //?
                authenticationToken = new
                        UsernamePasswordAuthenticationToken(username, user, userDetails.getAuthorities());
                authenticationToken.setDetails(userDetails);

                return authenticationToken;

            } else {
                throw new AppException("User has been deactivated");
            }
        }


           throw  new AppException("user has admin role!");
   }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);


    }

    /*THIS METHOD COMPARES TWO PASSWORD TO CHECK IF THE MATCH. THE PASSWORD IN THE SYSTEM
    AND THE PASSWORD SUPPLIED BY THE USER AT THE POINT OF AUTHENTICATION
     */
    private boolean checkPassword(User user, String rawPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println(matches);

        return matches;
    }

    public void handleFailedLogin(User user) {
        User u = userService.increaseLoginAttempt(user);
        int count = u.getLoginAttempt();
        System.out.println(count);
        if (count >= SecurityDetails.ALLOWED_LOGIN_ATTEMPTS) {
            //proceed to lock the user
            log.info("Proceeding to lock user");
            userService.lockUser(user);
        }


    }

    Optional<LockedUser> getUserById(User user) {
        return lockedUserRepository.findLockedUserById(user);

    }


}




