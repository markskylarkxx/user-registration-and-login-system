package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
final class UserSearchServiceImpl  implements  UserSearchService{
    @Override
    public List<User> findBySearchTerm(String searchTerm) {
       return  null;
    }
}
