package com.spring_boot_sec_app.session;

import com.spring_boot_sec_app.model.User;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service@Slf4j
public class SessionStorage {

    @Autowired
    private SessionRepository sessionRepository;
    String sessionId = UUID.randomUUID().toString() + System.currentTimeMillis();
    public void startSession(User user) {


        try {
            Session session = Session.builder()
                    .user(user)
                    .dateCreated(new Date())
                    .lastAccessed(new Date(System.currentTimeMillis()))
                    .expires(getSessionExpiryTime(new Date()))
                    .username(user.getUsername()).build();

            Optional<Session> existing = this.retrieveExistingSession(user.getUsername());
            if(!existing.isPresent()) {
                log.info("Session of user with username " +  user.getUsername() +  " has started");
                sessionRepository.save(session);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }


    }

    public void killSession(Session session) {
        Assert.notNull(session, "Session must be available");
        sessionRepository.deleteSession(session.getId());
    }

    public Optional<Session> retrieveExistingSession(String username) {
        return sessionRepository.findSessionByUsername(username);
    }

    public boolean checkSessionExpiry(Session session) {
        Date dateNow = new Date();
        Date sessionExpiryTime = session.getExpires();
        if(dateNow.getTime() > sessionExpiryTime.getTime()) {
            this.killSession(session);
            return true;
        }
//
//        if (dateNow.after(sessionExpiryTime)){
//            this.killSession(session);
//            return  true;
//        }

        //session has not yet expired - So update the last accessed date and the expiry time
        session.resetLastAccessDate();
        session.setExpires(this.getSessionExpiryTime(new Date()));
        sessionRepository.save(session);
        return false;
    }



    private Date getSessionExpiryTime(Date now) {
        return new Date(now.getTime() + 3 * 60000);
    }
}
