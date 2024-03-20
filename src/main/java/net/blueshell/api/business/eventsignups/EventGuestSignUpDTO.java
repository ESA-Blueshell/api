package net.blueshell.api.business.eventsignups;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.guest.Guest;

/**
 * DTO for signing up for an event without an account.
 */
@Data
public class EventGuestSignUpDTO {

    @JsonProperty
    private String name;

    @JsonProperty
    private String discord;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String email;

    @JsonProperty
    private String answers;

    public Guest toGuest() {
        return new Guest(name, discord, email, );
    }
}
