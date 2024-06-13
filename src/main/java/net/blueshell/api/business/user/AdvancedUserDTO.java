package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.picture.PictureDao;
import net.blueshell.api.storage.StorageService;
import net.blueshell.api.util.TimeUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for communicating account changes after the initial account.
 */
@Data
public class AdvancedUserDTO {

    public static final UserDao dao = new UserDao();

    private static final PictureDao pictureDao = new PictureDao();

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

    @JsonProperty
    private boolean ehbo;

    @JsonProperty
    private boolean bhv;

    @JsonProperty
    private String signature;

    @JsonProperty
    private Date signatureDate;

    @JsonProperty
    private String signatureCity;

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
    public User mapToBasicUser() {
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

    public void toMember(User user, StorageService storageService) {
        if (isEhbo()) {
            user.addRole(Role.EHBO);
        }
        if (isBhv()) {
            user.addRole(Role.BHV);
        }
        if (getDateOfBirth() != null) {
            user.setDateOfBirth(new Timestamp(getDateOfBirth().getTime()));
        }
        if (getPhoneNumber() != null) {
            user.setPhoneNumber(getPhoneNumber().replaceAll("\\s", ""));
        }
        if (getPostalCode() != null) {
            user.setPostalCode(getPostalCode());
        }
        if (getCity() != null) {
            user.setCity(getCity());
        }

        user.setNewsletter(isNewsletter());

        if (getSignature() != null) {
            // The signature comes from vue-signature-pad which always gives a PNG
            String filename = storageService.store(getSignature(), ".png");
            String downloadURL = StorageService.getDownloadURI(filename);

            Picture signature = new Picture(filename, downloadURL, user);
            pictureDao.create(signature);


            user.setSignature(signature);
            user.setSignatureDate(getSignatureDate());
            user.setSignatureCity(getSignatureCity());

            user.setConsentPrivacy(true); // Given that they signed the form they consent to our privacy policy

            // Given that they have signed the form they are now a member
            user.addRole(Role.MEMBER);
            user.setMemberSince(new Timestamp(System.currentTimeMillis()));
        }
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
