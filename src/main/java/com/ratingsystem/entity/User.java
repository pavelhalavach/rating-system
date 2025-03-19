package com.ratingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean isVerified;
    private boolean isEnabled;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @Enumerated(EnumType.STRING)
    private Role role;
}
