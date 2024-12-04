package net.blueshell.api.business.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.business.signature.SignatureDTO;

@EqualsAndHashCode(callSuper = true)
@Data
public class MemberDTO extends AdvancedUserDTO {

    @JsonProperty
    private boolean incasso;

    @JsonProperty
    private MemberType memberType;

    @JsonProperty
    private SignatureDTO signature;
}
