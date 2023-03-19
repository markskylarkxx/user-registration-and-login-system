package com.spring_boot_sec_app.repo;

import com.spring_boot_sec_app.model.Role;
import com.spring_boot_sec_app.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);


}

