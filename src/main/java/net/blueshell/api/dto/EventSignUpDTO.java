package net.blueshell.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import net.blueshell.api.base.DTO;
import net.blueshell.api.model.FormAnswer;

import java.util.List;


@Data
@Getter
public class EventSignUpDTO extends DTO {

    @JsonProperty
    private Long eventId;

    @JsonProperty
    private String fullName;

    @JsonProperty
    private String discord;

    @JsonProperty
    private String email;

    @JsonProperty
    @Valid
    private List<FormAnswer> formAnswers;
}
