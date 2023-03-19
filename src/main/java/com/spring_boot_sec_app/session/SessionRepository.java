package com.spring_boot_sec_app.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("select s from Session s where s.username = ?1")
    Optional<Session> findSessionByUsername(String username);

    @Modifying
    @Transactional

    @Query("delete  from Session s where s.id = ?1")
    void  deleteSession(long id);
}
