package net.blueshell.api.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.blueshell.api.dto.CommitteeMemberDTO;
import net.blueshell.api.dto.committee.AdvancedCommitteeDTO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdvancedCommitteeDTO.class, name = "committee"),
        @JsonSubTypes.Type(value = CommitteeMemberDTO.class, name = "committeeMember"),
})
public abstract class DTO extends AuthorizationBase {
}
