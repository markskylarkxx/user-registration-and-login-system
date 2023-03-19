package com.spring_boot_sec_app.service;

import com.spring_boot_sec_app.dto.ResetPassword;
import com.spring_boot_sec_app.exception.AppException;
import com.spring_boot_sec_app.exception.ResourceNotFoundException;
import com.spring_boot_sec_app.model.PasswordResetToken;
import com.spring_boot_sec_app.model.User;
import com.spring_boot_sec_app.repo.TokenRepo;
import com.spring_boot_sec_app.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j

public class PasswordResetService {
    @Value("${app.token}")
    private String resetToken;
    @Value("${app.sender}")
    private String sender;
    private static final long EXPIRE_TOKEN_AFTER_MINUTE = 3;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepo tokenRepo;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    //TODO: get the token expiry time(COME BACK!)
    private LocalDateTime getTokenExpiryTime(LocalDateTime now) {
        return LocalDateTime.now().plusMinutes(10);// expires 10 minutes after creation.
    }

    public String forgotPass(User user) {
        Optional<User> existingUser = userRepo.findUserByEmail(user.getEmail());
        if (!existingUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        User u = existingUser.get();
        //todo; get the list of unused token from the token table.
        List<PasswordResetToken> tokenOptional =
                tokenRepo.findByTokenAndUsed(u, Boolean.FALSE);
        System.out.println(tokenOptional.size());
        if (!tokenOptional.isEmpty()){
            tokenRepo.deleteByUsed(Boolean.FALSE);
            log.info("unused token have been deleted");
        }

        //Todo; delete all used token from the table;


// todo; if a user is already present in the token table do not create the user:



        // generate the token
        PasswordResetToken token = PasswordResetToken.builder().
                        token(resetToken).
                        dateCreated(LocalDateTime.now()).
                        expiryDate(getTokenExpiryTime(LocalDateTime.now())).
                        date_used(LocalDate.now()).used(false).
                        user(u).build();
        token.setToken(encoder.encode(token.getToken()));
        tokenRepo.save(token);

        // create a url link
        String appUrl = "http://localhost:4000/api/auth/reset?token=" + token.getToken();

        //Todo; create an email message
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo("kenechukwubanego@gmail.com");
            message.setText("To complete your password reset," +
                    "  please  click here\n: " + appUrl);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("message sent......");
        return "A password reset link has been  sent to " + user.getEmail();


    }


    public String resetPassword(ResetPassword resetPassword, Long userId) {

        //todo; get all  token by user id and used status;
        User user = userRepo.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User Not found"));

        List<PasswordResetToken> list = tokenRepo.findByUserIdAndUsed(user, Boolean.FALSE);
        PasswordResetToken token = null;
        //todo; iterate through the list and match the token supplied to the token in the database;
        if (!ObjectUtils.isEmpty(list)) {// List is not empty, loop through the list using for each loop
            for (PasswordResetToken tok : list) {
                token = tok;
                if (!encoder.matches(resetPassword.getToken(), token.getToken())) {
                    throw new ResourceNotFoundException("Token supplied did not match!");
                }
                if (isTokenExpired(token)) {
                    throw new AppException("Token has expired");
                }
                if (checkIfTokenIsExpired(token)) {
                    throw new AppException("Token is expired!");
                }
                //verify if password and confirm password match

                boolean equals = resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword());
                System.out.println(equals);
                if (!resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {
                    throw new AppException("Password do not match");
                }
            }
//            //todo; set the used status of the token to true and also set the token value to null;
            token.setUsed(true);
            token.setToken(null);

//            // todo; save the new password to the user table
            user.setPassword(encoder.encode(resetPassword.getNewPassword()));
            userRepo.save(user);
        }
        return "Your password has been successfully reset";
    }
    //todo; method to match password supplied to that in the database
    private boolean isTokenExpired (PasswordResetToken  resetToken){

        //return resetToken.getDateCreated().isBefore(LocalDateTime.now());
        return  resetToken.getExpiryDate().getMinute() <
                resetToken.getDateCreated().plusMinutes(LocalDateTime.now().getMinute()).getMinute();
    }
    private boolean checkIfTokenIsExpired (PasswordResetToken resetToken){
        LocalDateTime dateCreated = resetToken.getDateCreated();
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(dateCreated, now);
        return diff.toMinutes() > resetToken.getExpiryDate().getMinute();
    }



}




