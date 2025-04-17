package net.blueshell.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
public class EventSignUpDTO extends BaseDTO {
    private Long id;
    private Long eventId;
    private String fullName;
    private String discord;
    private String email;
    @Valid
    private List<Object> formAnswers;
}
