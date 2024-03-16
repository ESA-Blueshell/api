package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import net.blueshell.api.tables.records.CommitteesRecord;

public class CommitteeDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("members")
    private List<CommitteeMembershipDTO> members;

    private CommitteeDTO() {
    }

    public CommitteesRecord toCommittee() {
        CommitteesRecord res = new CommitteesRecord();
        res.setName(name);
        res.setDescription(description);
        res.setMembers(members.stream().map(membershipDTO -> membershipDTO.toMembership(res)).collect(Collectors.toSet()));
        return res;
    }

    public static CommitteeDTO fromCommittee(CommitteesRecord committee) {
        CommitteeDTO res = new CommitteeDTO();
        res.id = committee.getId();
        res.name = committee.getName();
        res.description = committee.getDescription();
        res.members = committee.getMembers().stream().map(CommitteeMembershipDTO::fromMembership).collect(Collectors.toList());
        return res;
    }
}

