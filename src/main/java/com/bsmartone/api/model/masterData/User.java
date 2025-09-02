package com.bsmartone.api.model.masterData;

import com.bsmartone.api.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Audited
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_taxid", columnList = "taxid"),
    @Index(name = "idx_user_email", columnList = "email")
})
public class User extends BaseEntity implements Serializable {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String taxid;

    @Column(nullable = false)
    private String fullname;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status", nullable = false)   // FK a user_status.id (ajusta nombre)
    private UserStatus status;
    @Column
    private Long bsmartoneprofile;

    @Column
    private String cityofresidence;

    @Column
    private String address;

    @Column
    private Integer failedattempts;

    @Column
    private LocalDateTime startdate;

    @Column
    private LocalDateTime enddate;

    @Column
    private String cellphone;

    @Column
    private boolean requirespasswordchange;

    @Transient
    @Builder.Default
    private Set<Role> roles = new java.util.HashSet<>();

    public User() {
    }

    @Builder
    public User(Long id, String username, String password, String email, String taxid, String fullname, UserStatus status, Long bsmartoneprofile, String cityofresidence, String address, Integer failedattempts, LocalDateTime startdate, LocalDateTime enddate, String cellphone, Set<Role> roles, boolean requirespasswordchange, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.taxid = taxid;
        this.fullname = fullname;
        this.status = status;
        this.bsmartoneprofile = bsmartoneprofile;
        this.cityofresidence = cityofresidence;
        this.address = address;
        this.failedattempts = failedattempts;
        this.startdate = startdate;
        this.enddate = enddate;
        this.cellphone = cellphone;
        this.roles = roles;
        this.requirespasswordchange = requirespasswordchange;
    }
}
