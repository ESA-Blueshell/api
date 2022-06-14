package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

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
    private Timestamp dateOfBirth;

    @JsonProperty
    private String discordTag;

    @JsonProperty
    private String email;

    @JsonProperty
    private String phoneNumber;

    @JsonProperty
    private String street;

    @JsonProperty
    private String houseNumber;

    @JsonProperty
    private String postalCode;

    @JsonProperty
    private String city;

    @JsonProperty
    private String country;

    @JsonProperty
    private boolean wantsNewsletter;

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
    public User mapToUser() {
        User user;
        if (getId() != 0) {
            user = dao.getById(getId());
        } else {
            user = new User();
            user.setUsername(getUsername());
        }

        if (getPassword() != null) {
            user.setPassword(passwordEncoder.encode(getPassword()));
        }
        if (getEmail() != null) {
            user.setEmail(getEmail());
        }
        if (getDiscordTag() != null) {
            user.setDiscord(getDiscordTag());
        }
        return user;
    }

    public static AdvancedUserDTO fromUser(User user) {
        var res = new AdvancedUserDTO();
        res.id = user.getId();
        res.username = user.getUsername();
        res.email = user.getEmail();
        res.discordTag = user.getDiscord();
        return res;
    }
}
