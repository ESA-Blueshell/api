package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.business.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;


public class EventSignUpDTO implements Serializable {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("discord")
    private String discord;

    @JsonProperty("formAnswers")
    private String formAnswers;

    @JsonProperty("signedUpAt")
    private LocalDateTime signedUpAt;

    private EventSignUpDTO() {
    }


    public static EventSignUpDTO fromSignUp(EventSignUp signUp) {
        EventSignUpDTO res = new EventSignUpDTO();

        User user = signUp.getUser();

        //TODO: use SimpleUserDTO
        res.fullName = signUp.getUser().getFullName();
        res.discord = user.getDiscord();

        res.formAnswers = signUp.getFormAnswers();
        res.signedUpAt = signUp.getSignedUpAt();

        return res;
    }
}
