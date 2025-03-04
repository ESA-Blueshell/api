package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;
import net.blueshell.api.dto.committee.AdvancedCommitteeDTO;
import net.blueshell.api.dto.user.SimpleUserDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommitteeMemberDTO extends DTO {
    private String role;
    private long userId;
    private SimpleUserDTO user;
    private long committeeId;
}
