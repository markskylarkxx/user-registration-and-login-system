package com.spring_boot_sec_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class MailConfiguration {

        private  String username ="markskylarkxx@gmail.com";
    private String password = "nxyixccgzvcfrmpa";
//    private String username = "obiora.okwubanego@unionsystems.com";
//    private String password = "out514801Reg_Num";

    @Bean

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    mailSender.setHost("smtp.office365.com");
//    mailSender.setPort(587);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.stmp.user", username);

        //To use TLS
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.password", password);
        //To use SSL
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        Session session = Session.getDefaultInstance(props, null);
        String to = "kenechukwubanego@gmail.com";
        String from = username;
        String subject = "Testing...";
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText("Working fine..!");
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", 465, username, password);
            transport.send(msg, username, password);
            System.out.println("fine!!");
        } catch (Exception exc) {
            System.out.println(exc);
        }
        return mailSender;
    }
}