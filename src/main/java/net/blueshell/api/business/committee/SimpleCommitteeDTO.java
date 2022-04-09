package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleCommitteeDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;


    private SimpleCommitteeDTO() {
    }

    public static SimpleCommitteeDTO fromCommittee(Committee committee) {
        SimpleCommitteeDTO res = new SimpleCommitteeDTO();
        res.name = committee.getName();
        res.description = committee.getDescription();
        return res;
    }
}

