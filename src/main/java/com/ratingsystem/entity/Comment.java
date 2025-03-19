package com.ratingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String message;
    private Integer rating;
    @ManyToOne
    @JoinColumn(name = "addressed_to_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User seller;
    private boolean isVerified;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
