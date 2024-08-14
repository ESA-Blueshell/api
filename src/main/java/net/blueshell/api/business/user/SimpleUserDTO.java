package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.Email;

@Data
public class SimpleUserDTO {

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
    private String prefix;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String fullName;

    @JsonProperty
    private String password;

    @JsonProperty
    @Email
    private String email;


    private SimpleUserDTO() {
    }

    public static SimpleUserDTO fromUser(User user) {
        SimpleUserDTO res = new SimpleUserDTO();
        res.id = user.getId();
        res.username = user.getUsername();
        res.discord = user.getDiscord();
        res.firstName = user.getFirstName();
        res.prefix = user.getPrefix();
        res.lastName = user.getLastName();
        res.fullName = user.getFullName();
        res.password = user.getPassword();
        res.email = user.getEmail();
        return res;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toUser() {
        return dao.getByUsername(username);
    }
}
