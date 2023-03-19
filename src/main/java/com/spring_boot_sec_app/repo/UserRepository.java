package com.spring_boot_sec_app.repo;

import com.spring_boot_sec_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>{


    void delete(User deleted);
    List<User>  findAll();

//    Optional<User> findOne();

    void flush();

    User save(User persisted);



    @Query("select  u from  User u where  u.username = ?1")
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("select  u from  User  u where u.resetToken=?1")
    Optional <User>findByResetToken(String resetToken);

    ///Password_controller
    @Query("select u from  User u where  u.email = ?1")
    Optional<User>   findUserByEmail(String email);


    @Query("select u from   User  u where u.id =?1")
    Optional<User> findById(Long id);

}

