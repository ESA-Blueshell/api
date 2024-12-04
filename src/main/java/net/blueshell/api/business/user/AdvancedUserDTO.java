package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.signature.SignatureDTO;
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
    private String initials;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String prefix;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private Timestamp dateOfBirth;

    @JsonProperty
    private String discord;

    @JsonProperty
    private String steamid;

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

    @JsonProperty
    private boolean ehbo;

    @JsonProperty
    private boolean bhv;

    @JsonProperty
    private boolean incasso;

    @JsonProperty
    private MemberType memberType;

    @JsonProperty
    private SignatureDTO signature;

    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
