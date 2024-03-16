package net.blueshell.api.business.eventsignups;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;


public class EventSignUpDTO implements Serializable {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("discord")
    private String discord;

    @JsonProperty("email")
    private String email;

    @JsonProperty("formAnswers")
    private String formAnswers;

    @JsonProperty("signedUpAt")
    private LocalDateTime signedUpAt;

    private EventSignUpDTO() {
    }


    public static EventSignUpDTO fromSignUp(EventSignUp signUp) {
        EventSignUpDTO res = new EventSignUpDTO();

        if (signUp.getUser() != null) {
            //TODO: use SimpleUserDTO
            res.fullName = signUp.getUser().getFullName();
            res.discord = signUp.getUser().getDiscord();
            res.email = signUp.getUser().getEmail();
        } else {
            res.fullName = signUp.getGuest().getName();
            res.discord = signUp.getGuest().getDiscord();
            res.email = signUp.getGuest().getEmail();
        }

        res.formAnswers = signUp.getFormAnswers();
        res.signedUpAt = signUp.getSignedUpAt();

        return res;
    }
}
