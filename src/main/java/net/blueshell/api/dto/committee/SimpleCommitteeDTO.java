package net.blueshell.api.dto.committee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.base.DTO;

@Getter
@Setter
public class SimpleCommitteeDTO extends DTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    public SimpleCommitteeDTO() {
    }
}
