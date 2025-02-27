package net.blueshell.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;
import net.blueshell.api.base.DTO;

@Data
public class SimpleUserDTO extends DTO {

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
    @Email
    private String email;
}
