package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.tables.records.CommitteesRecord;

public class SimpleCommitteeDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;


    private SimpleCommitteeDTO() {
    }

    public static SimpleCommitteeDTO fromCommittee(CommitteesRecord committee) {
        SimpleCommitteeDTO res = new SimpleCommitteeDTO();
        res.id = committee.getId();
        res.name = committee.getName();
        res.description = committee.getDescription();
        return res;
    }
}

