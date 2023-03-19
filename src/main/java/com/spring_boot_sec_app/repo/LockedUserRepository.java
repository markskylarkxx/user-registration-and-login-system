package com.spring_boot_sec_app.repo;

import com.spring_boot_sec_app.model.LockedUser;
import com.spring_boot_sec_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockedUserRepository extends JpaRepository<LockedUser, Long> {


    @Query("select u from  LockedUser u where  u.userId = ?1")
    Optional<LockedUser> findLockedUserById(User user);

    @Query(value = "select l from  LockedUser l")
    @Transactional
    List<LockedUser> findAll();

    @Query("delete  from LockedUser  u where  u.userId = ?1")
    @Modifying
    @Transactional
    void deleteLockedUserByUserId(User user);

    @Query("select l from LockedUser l where l.userId = ?1")
    Optional<LockedUser> findLockedUserByUserId(Long userId);
}

