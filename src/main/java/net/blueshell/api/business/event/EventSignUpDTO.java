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


    public static EventSignUpDTO fromSignUp(EventSignUp signUp) {
        EventSignUpDTO res = new EventSignUpDTO();

        User user = signUp.getUser();

        if (user.getPrefix() != null) {
            //TODO: when sign-up is implemented, check if user.prefix can even be null
            res.fullName = user.getFirstName() + " " + user.getPrefix() + " " + user.getLastName();
        } else {
            res.fullName = user.getFirstName() + " " + user.getLastName();
        }
        res.discord = user.getDiscord();
        res.formAnswers = signUp.getFormAnswers();
        res.signedUpAt = signUp.getSignedUpAt();

        return res;
    }
}
