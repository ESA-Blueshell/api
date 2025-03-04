package net.blueshell.api.dto.committee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleCommitteeDTO extends DTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
