package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.committee.CommitteeMembership;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.util.TimeUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class User implements UserDetails {

    private long id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String prefix;

    private String initials;

    private String address;

    private String houseNumber;

    private String postalCode;

    private String city;

    private String phoneNumber;

    private String email;

    private String studentNumber;

    private LocalDateTime dateOfBirth;

    private LocalDateTime createdAt;

    private LocalDateTime memberSince;

    private String discord;

    private String steamid;

    private boolean newsletter;

    private boolean enabled;

    @JsonIgnore
    private String resetKey;

    @JsonIgnore
    private LocalDateTime resetKeyValidUntil;

    @JsonIgnore
    private ResetType resetType;

    private boolean contributionPaid;

    private boolean consentPrivacy;

    private boolean consentGdpr;

    private String gender;

    private String street;

    private String country;

    private boolean photoConsent;

    private String nationality;

    private String study;

    private int startStudyYear;

    @JsonIgnore
    private Picture profilePicture;

    private LocalDateTime deletedAt;

    @JsonIgnore
    private Set<CommitteeMembership> committeeMemberships;

    private Set<Role> roles;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String email) {
        this();
        createdAt = LocalDateTime.now();
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
            set.add(cm.getCommitteeId());
        }
        return set;
    }

    @JsonProperty("roles")
    public Set<String> getRoleStrings() {
        Set<String> set = new HashSet<>();
        if (getRoles() == null) {
            return set;
        }
        // Go through all inherited roles
        for (Role role : roles.stream().flatMap(role -> role.getAllInheritedRoles().stream()).collect(Collectors.toList())) {
            set.add(role.getReprString());
        }
        return set;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
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

    public void addRole(Role role) {
        getRoles().add(role);
    }

    public void removeRole(Role role) {
        getRoles().remove(role);
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
        return enabled && (getDeletedAt() == null || !TimeUtil.hasExpired(getDeletedAt()));
    }

    public String getFullName() {
        if (prefix == null || prefix.isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + prefix + " " + lastName;
    }

}
