package com.spring_boot_sec_app.controller;

import com.spring_boot_sec_app.dto.Deactivated;
import com.spring_boot_sec_app.dto.SearchRequest;
import com.spring_boot_sec_app.model.LockedUser;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class UserController {


    @Autowired
    private UserServiceImpl userService;
    @Autowired
    UserRepository repository;
    @GetMapping("/all/users")
    public  List<User> finAll(){
        return  repository.findAll();
    }



    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{username}")
    public ResponseEntity getUsername(@PathVariable String username, Principal principal) {
        System.out.println(principal.getName());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasUserRole = authentication.getAuthorities().
                stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"));
        System.out.println(hasUserRole);
        System.out.println(principal.getName());

        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/locked")
    public List<LockedUser> findAllLockedUser(Principal principal) {
        System.out.println(principal.getName());
        return userService.getAllLockedUser();

    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{user_id}")
    public void unlockUser(@PathVariable("user_id") User user) {
        userService.unlockUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/locked/{user_id}")
    public Optional<LockedUser> findLockedUser(@PathVariable("user_id")Long userId) {

        return userService.findLockedUserByUserId(userId);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deactivate/{user_id}")
    public void deactivateUser(@RequestBody Deactivated deactivated,
                               @PathVariable("user_id") User user, Principal principal) {
        Assert.notNull(deactivated, "Deactivation reason must be provided");
        System.out.println(principal.getName());

        userService.deactivateUser(deactivated, user, principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("activate/{user_id}")
    public void activateUser(@PathVariable("user_id") User user) {
        userService.activateUser(user);
    }
    @GetMapping("/it")
    public  String getCurrentUser(Principal principal){
        return principal.getName();
    }


        @PostMapping("/search")
    public ResponseEntity search(@RequestBody User user) {
        return ResponseEntity.ok(userService.search(user));
    }

    @GetMapping("/searchByAny")
    public ResponseEntity search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(userService.searchByAnyItem(request));
    }

    @PostMapping("/get")
    public  ResponseEntity searchItem(@RequestBody User user){
        return  ResponseEntity.ok(userService.findBySearchTerm(user));
    }
    // get currrently logged in user
      @GetMapping("/principal")
    public  void getLoggedInUser(Principal principal){
        System.out.println(principal.getName());
    }
}
