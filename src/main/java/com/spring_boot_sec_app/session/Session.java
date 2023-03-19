package com.spring_boot_sec_app.session;

import com.spring_boot_sec_app.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessed;

    @Temporal(TemporalType.TIMESTAMP)
    private Date  expires;

//    @Column(name = "session_id", unique = true)
//    private String sessionId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void resetLastAccessDate() {
        this.lastAccessed = new Date();
    }
}

