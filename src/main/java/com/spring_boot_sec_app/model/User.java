package com.spring_boot_sec_app.model;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.spring_boot_sec_app.dto.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank
    @Size(max = 40)
    private String name;

    // @NotBlank
    @Size(max = 15)
    @Column(name = "username", unique = true)
    private String username;

    @NaturalId
    //@NotBlank
    @Size(max = 40)
    @Email
    private String email;

    //@NotBlank
    @Size(max = 100)
    // @JsonIgnore
    @Column(unique = true)
    private String password;
    @Column(name = "failed_attempt",
            columnDefinition = "integer default 0")
    private int loginAttempt;

    @Column(name = "is_active")
    private Boolean active;
    private String passport;
    //////////////
    @Column(name = "reset_token")
    private String resetToken;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenCreationDate;
    ///////////////
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String name, String username, String email, String password, String passport) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passport = passport;

    }

    public User(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;


    }

    public void setFailedLoginAttempts() {
        // when this method is called the login attempt increases by 1;
        this.loginAttempt++;
    }

    public BooleanBuilder predicates() {
        BooleanBuilder builder = new BooleanBuilder();
        QUser qUser = QUser.user;

        if(!StringUtils.isNullOrEmpty(this.getEmail())) {
            builder.and(qUser.email.containsIgnoreCase(this.getEmail()));
        }

        if(!StringUtils.isNullOrEmpty(this.getUsername())) {
            builder.and(qUser.username.containsIgnoreCase(this.getUsername()));
        }

        if(!ObjectUtils.isEmpty(this.getActive())) {
            builder.and(qUser.active.eq(this.getActive() == null ? Boolean.FALSE : this.getActive()));
        }

       return builder;

    }

    public BooleanBuilder anyPredicates(SearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();
        QUser qUser = QUser.user;
        if(!StringUtils.isNullOrEmpty(searchRequest.getSearchItem())) {
            builder.or(qUser.username.containsIgnoreCase(searchRequest.getSearchItem()));
            builder.or(qUser.email.containsIgnoreCase(searchRequest.getSearchItem()));
            builder.or(qUser.name.containsIgnoreCase(searchRequest.getSearchItem()));
        }
        return builder;
    }
        // update a field using query dsl;






}
