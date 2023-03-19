package com.spring_boot_sec_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long  tokenId;

    private  String  token;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_used")
    private boolean used;

    @Column(name = "date_used")
    private LocalDate date_used;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;


}

