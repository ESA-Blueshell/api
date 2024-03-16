package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.tables.records.UsersRecord;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for communicating account changes after the initial account.
 */
@Data
public class AdvancedUserDTO {

    public static final UserDao dao = new UserDao();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @JsonProperty
    private long id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String initials;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String prefix;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private LocalDateTime dateOfBirth;

    @JsonProperty
    private String discord;

    @JsonProperty
    private String steamId;

    @JsonProperty
    private String email;

    @JsonProperty
    private String phoneNumber;

    @JsonProperty
    private String postalCode;

    @JsonProperty
    private String address;

    @JsonProperty
    private String city;

    @JsonProperty
    private String country;

    @JsonProperty
    private boolean newsletter;

    @JsonProperty
    private boolean photoConsent;

    @JsonProperty
    private String nationality;

    @JsonProperty
    private String schoolMail;

    @JsonProperty
    private String studentNumber;

    @JsonProperty
    private String study;

    @JsonProperty
    private int startStudyYear;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Creates a new User if id == 0, gets the current User from the DB otherwise. Then fills all fields with the ones
     * of this object *if they are not null*. If a field *is null*, the field will be ignored.
     * @return the created or altered User
     */
    public net.blueshell.api.tables.records.UsersRecord mapToBasicUserRecord() {
        net.blueshell.api.tables.records.UsersRecord user;
        if (getId() != 0) {
            user = dao.getRecordById(getId());
        } else {
            user = new UsersRecord();
            user.setUsername(getUsername());
        }

        if (getPassword() != null) {
            user.setPassword(passwordEncoder.encode(getPassword()));
        }
        if (getInitials() != null) {
            user.setInitials(getInitials());
        }
        if (getFirstName() != null) {
            user.setFirstName(getFirstName());
        }
        if (getPrefix() != null) {
            user.setPrefix(getPrefix());
        }
        if (getLastName() != null) {
            user.setLastName(getLastName());
        }
        if (getEmail() != null) {
            user.setEmail(getEmail());
        }
        if (getDiscord() != null) {
            user.setDiscord(getDiscord());
        }
        return user;
    }

    public static AdvancedUserDTO fromUser(User user) {
        var res = new AdvancedUserDTO();
        res.id = user.getId();
        res.username = user.getUsername();
        res.email = user.getEmail();
        res.discord = user.getDiscord();
        return res;
    }
}
