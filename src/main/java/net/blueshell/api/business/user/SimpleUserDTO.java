package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleUserDTO {

    public static final UserDao dao = new UserDao();

    @JsonProperty("username")
    private String username;

    @JsonProperty("discord")
    private String discord;

    @JsonProperty("fullName")
    private String fullName;


    private SimpleUserDTO() {
    }

    public User toUser() {
        return dao.getByUsername(username);
    }

    public static SimpleUserDTO fromUser(User user) {
        SimpleUserDTO res = new SimpleUserDTO();
        res.username = user.getUsername();
        res.discord = user.getDiscord();
        res.fullName = user.getFullName();
        return res;
    }
}
