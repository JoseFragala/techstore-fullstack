package com.fragala.techstore.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column (nullable = false)
    private String name;

    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false)
    private String password;

    @Setter
    @Column (nullable = false)
    private String phone;

    @Column (nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column (nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Setter
    @JoinColumn(name = "role_id", nullable = false)
    @ManyToOne
    private Role role;
    

    public void activate(){
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    public void changePassword(String hashedPassword){
        this.password = hashedPassword;
    }
    public void changeEmail(String email){
        this.email = email;
    }
}
