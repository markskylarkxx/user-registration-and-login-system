package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.model.User;

import java.util.List;

public interface UserSearchService {

    List<User> findBySearchTerm(String searchTerm);
}
