package net.blueshell.api.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String prefix;

    private String initials;

    private String address;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "postal_code")
    private String postalCode;

    private String city;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    @Column(name = "student_number")
    private String studentNumber;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "member_since")
    private Timestamp memberSince;

    private String discord;

    private String steamId;

    private boolean newsletter;

    @Column(name = "contribution_paid")
    private boolean contributionPaid;

    @Column(name = "consent_privacy")
    private boolean consentPrivacy;

    @Column(name = "consent_gdpr")
    private boolean consentGdpr;

    @OneToOne
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @OneToOne
    @JoinColumn(name = "profile_picture")
    private Picture profilePicture;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


}
