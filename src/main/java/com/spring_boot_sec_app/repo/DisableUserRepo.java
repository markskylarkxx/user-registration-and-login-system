package com.spring_boot_sec_app.repo;

import com.spring_boot_sec_app.model.DisabledUser;
import com.spring_boot_sec_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DisableUserRepo extends JpaRepository<DisabledUser, Long> {

    @Query("delete  from DisabledUser  u where  u.userId = ?1")
    @Modifying
    @Transactional
    void deleteDeactivatedUserByUserId(User user);
}
