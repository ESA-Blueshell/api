package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * DTO for communicating account changes after the initial account.
 */
@Data
public class AdvancedUserDTO {

    public static final UserDao dao = new UserDao();

    @JsonProperty
    private long id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String discord;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String password;

    @JsonProperty
    @Email
    private String email;
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toUser() {
        User user;
        if (getId() != 0)
        {
            user = dao.getById(getId());
        }
        else {
            user = new User();
            user.setUsername(getUsername());
        }
        user.setPassword(getPassword());
        user.setFirstName(getFirstName());
        user.setLastName(getLastName());
        user.setEmail(getEmail());
        user.setDiscord(getDiscord());
        return user;
    }

    public static AdvancedUserDTO fromUser(User user) {
        var res = new AdvancedUserDTO();
        res.id = user.getId();
        res.username = user.getUsername();
        res.discord = user.getDiscord();
        res.firstName = user.getFirstName();
        res.lastName = user.getLastName();
        res.email = user.getEmail();
        return res;
    }
}
