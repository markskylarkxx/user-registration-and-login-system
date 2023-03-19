package com.spring_boot_sec_app.repo;

import com.spring_boot_sec_app.model.PasswordResetToken;
import com.spring_boot_sec_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<PasswordResetToken, Long> {
    @Query("delete from  PasswordResetToken t where t.tokenId =?1")
    void deletePasswordResetToken(Long tokenId);

    @Query("select p from  PasswordResetToken p where p.token =?1" )
    Optional<PasswordResetToken> findByToken(String token);

    @Query("select p from  PasswordResetToken p where p.user=?1 and p.used = ?2")
    List<PasswordResetToken> findByUserIdAndUsed(User user, boolean used);

    @Query("select p from  PasswordResetToken p where p.used=?1")
    List<PasswordResetToken> findByUsed( boolean used);


    List< PasswordResetToken> findByTokenIdContaining(Long userId);
    @Query("select  p from PasswordResetToken  p where p.used=?1")
    List<PasswordResetToken> findTokenByUsed(Boolean used);
    @Query("select  p from  PasswordResetToken p where  p.user =?1 and p.used = ?2")
    List<PasswordResetToken> findByTokenAndUsed(User user, Boolean a);
    @Query("delete  from  PasswordResetToken  p where p.used =?1")
    @Modifying
    @Transactional
    void deleteByUsed(boolean used);



}
