package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;
import net.blueshell.api.dto.committee.AdvancedCommitteeDTO;
import net.blueshell.api.dto.user.SimpleUserDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommitteeMemberDTO extends BaseDTO {
    private Long id;
    private String role;
    private Long userId;
    private SimpleUserDTO user;
    private Long committeeId;
}
