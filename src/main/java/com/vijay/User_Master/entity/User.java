package com.vijay.User_Master.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This ID is used as business_id for ROLE_OWNER
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String phoNo;
    @Column(length = 1000)
    private String about;
    private String imageName;
    private boolean isDeleted;
    private LocalDateTime deletedOn;
    
    // Business/Hotel information fields (for ROLE_OWNER)
    @Column(length = 200)
    private String businessName; // Hotel/Restaurant name
    
    @Column(length = 500)
    private String businessAddress;
    
    @Column(length = 15)
    private String businessPhone;
    
    @Column(length = 100)
    private String businessEmail;
    
    @Column(length = 50)
    private String businessType; // HOTEL, RESTAURANT, BOTH
    
    @Column(length = 1000)
    private String businessDescription;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Worker> workers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "status_id")
    private AccountStatus accountStatus;
}

