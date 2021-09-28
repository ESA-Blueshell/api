package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleUserDTO {

    @JsonIgnore
    private User user;

    public SimpleUserDTO(User user) {
        this.user = user;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonProperty("email")
    public String getEmail() {
        return user.getEmail();
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return user.getFirstName();
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return user.getLastName();
    }

    @JsonProperty("profilePicture")
    public long getProfilePicture() {
        return user.getProfilePictureId();
    }


}
