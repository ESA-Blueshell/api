package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.billable.Billable;
import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.committee.CommitteeMembership;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.registration.Registration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    @JsonIgnore
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
    @JsonIgnore
    private Registration registration;

    @OneToOne
    @JoinColumn(name = "profile_picture")
    @JsonIgnore
    private Picture profilePicture;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<CommitteeMembership> committeeMemberships;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "committee_id")
    )
    @JsonIgnore
    private Set<Committee> subscriptions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "source")
    @JsonIgnore
    private Set<Billable> billables;

//    @JoinTable(
//            name = "authorizations",
//            joinColumns = @JoinColumn(name = "user_id")
//    )
//    @ElementCollection(targetClass = Role.class)
//    @Enumerated(EnumType.STRING)
//    @JsonIgnore
    @Transient
    private Set<Role> roles;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String email) {
        this();
        createdAt = Timestamp.from(Instant.now());
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("registration")
    public long getRegistrationId() {
        return getRegistration() == null ? 0 : getRegistration().getId();
    }

    @JsonProperty("profilePicture")
    public long getProfilePictureId() {
        return getProfilePicture() == null ? 0 : getProfilePicture().getId();
    }

    @JsonProperty("committees")
    public Set<Long> getCommitteeIds() {
        Set<Long> set = new HashSet<>();
        if (getCommitteeMemberships() == null) {
            return set;
        }
        for (CommitteeMembership cm : getCommitteeMemberships()) {
            set.add(cm.getUserId());
        }
        return set;
    }

    @JsonProperty("subscriptions")
    public Set<Long> getSubscriptionIds() {
        Set<Long> set = new HashSet<>();
        if (getSubscriptions() == null) {
            return set;
        }
        for (Committee sub : getSubscriptions()) {
            set.add(sub.getId());
        }
        return set;
    }

    @JsonProperty("billables")
    public Set<Long> getBillableIds() {
        Set<Long> set = new HashSet<>();
        if (getBillables() == null) {
            return set;
        }
        for (Billable billable : getBillables()) {
            set.add(billable.getId());
        }
        return set;
    }

    @JsonProperty("roles")
    public Set<String> getRoleStrings() {
        Set<String> set = new HashSet<>();
        if (getRoles() == null) {
            return set;
        }
        for (Role role : getRoles()) {
            set.add(role.getReprString());
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean hasRole(Role role) {
        return getRoles().stream().anyMatch(r -> r.matchesRole(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        var auths = new HashSet<GrantedAuthority>();
        if (getRoles() == null) {
            return auths;
        }

        for (var role : getRoles()) {
            auths.add(new SimpleGrantedAuthority(role.getReprString()));
        }

        return auths;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getDeletedAt() != null && getDeletedAt().before(Timestamp.from(Instant.now()));
    }
}
