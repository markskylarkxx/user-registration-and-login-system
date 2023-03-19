package com.spring_boot_sec_app.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring_boot_sec_app.dto.Deactivated;
import com.spring_boot_sec_app.dto.SearchRequest;
import com.spring_boot_sec_app.exception.AppException;
import com.spring_boot_sec_app.exception.ResourceNotFoundException;
import com.spring_boot_sec_app.mapper.UserMapper;
import com.spring_boot_sec_app.model.DisabledUser;
import com.spring_boot_sec_app.model.LockedUser;
import com.spring_boot_sec_app.model.QUser;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.dto.UserDTO;
import com.spring_boot_sec_app.predicate.UserPredicate;
import com.spring_boot_sec_app.repo.DisableUserRepo;
import com.spring_boot_sec_app.repo.LockedUserRepository;
import com.spring_boot_sec_app.repo.TokenRepo;
import com.spring_boot_sec_app.repo.UserRepository;
import com.spring_boot_sec_app.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@Slf4j
public class UserServiceImpl {

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 2;
    @Autowired
    DisableUserRepo userDeactivationRepo;
    @Autowired
    private EmailServiceImpl emailService;

    @Autowired

    private TokenRepo tokenRepo;
    @Autowired
    private LockedUserRepository lockedUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    DisableUserRepo disableUserRepo;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public UserPrincipal loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not Found"));

        return UserPrincipal.create(user);
    }


    public User increaseLoginAttempt(User user) {
        user.setFailedLoginAttempts();
        return userRepository.save(user);
    }

    public void lockUser(User user) {
        if (user.getActive()) {
            LockedUser lockedUser = LockedUser.builder()
                    .userId(user).lockedTime(LocalDateTime.now())
                    .build();
            lockedUserRepository.save(lockedUser);
        }
    }

    // GET ALL THE LOCKED USER;
    public List<LockedUser> getAllLockedUser() {
        List<LockedUser> lockedUser = lockedUserRepository.findAll();
        if (lockedUser.isEmpty()) {
            throw new AppException("No Locked user found in the database!");
        } else
            return lockedUser;

    }

    // unlock user(only admin)
    public void unlockUser(User user) {
        lockedUserRepository.deleteLockedUserByUserId(user);
        log.info("User unlocked!");

    }

    // GET LOCKED USER BY USER ID;
    public Optional<LockedUser> findLockedUserByUserId(Long userId) {
        return lockedUserRepository.findLockedUserByUserId(userId);

    }


    public void deactivateUser(Deactivated deactivated,
                               User user,
                               Principal principal) {

        if (user.getActive() == Boolean.TRUE) {
            user.setActive(Boolean.FALSE);
            userRepository.save(user);
            log.info("user has been deactivated");
        }
        DisabledUser disabledUser = DisabledUser.builder().
                username(principal.getName()).
                reason_for_deactivation(deactivated.getDeactivationReason()).
                userId(user).date(LocalDateTime.now()).build();
         disableUserRepo.save(disabledUser);
    }

    public void activateUser(User user) {
        disableUserRepo.deleteDeactivatedUserByUserId(user);
        user.setActive(Boolean.TRUE);
        userRepository.save(user);

    }

    /////////////////////////////////// ForgetPassword///////////////////////////////////////
    public String forgetPassword(String email) {

        // look up user in the database
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if (!existingUser.isPresent()) {
            return "Invalid email address";

        }
        // if present, get the existing user.
        User user = existingUser.get();
        //generate the token
        user.setResetToken(UUID.randomUUID().toString());
        //set  creation date;
        user.setTokenCreationDate(LocalDateTime.now());
        // save  the user;
        user = userRepository.save(user);
        return user.getResetToken();
    }

    ////TODO: RESET THE PASSWORD
    public String resetPassword(String token,
                                String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (!userOptional.isPresent()) {
            return " Invalid Token";
        }

        // get the token creation time;
        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();
        if (isTokenExpired(tokenCreationDate)) {
            return "Token is expired";
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
        return "Your password is saved successfully";

    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);
        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    public Page<User> search(User user) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        return userRepository.findAll(user.predicates(), pageRequest);
    }



    public Iterable <User>  searchByAnyItem(SearchRequest request){
        Iterable<User> all = userRepository.findAll(new User().anyPredicates(request));
                return all;
    }

   public List<UserDTO> findBySearchTerm(User user){
       Predicate searchPred = (Predicate) new UserPredicate().searchByName(user.getName());
     // BooleanExpression searchPr =  new UserPredicate().searchByName(user.getName());

       Iterable<User> searchResult = userRepository.findAll((Sort) searchPred);
       return (List<UserDTO>) UserMapper.mapToUserDto((User) searchResult);

   }
}






