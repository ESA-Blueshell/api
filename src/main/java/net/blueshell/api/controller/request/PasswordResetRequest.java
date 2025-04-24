package net.blueshell.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.dto.BaseDTO;
import net.blueshell.api.validation.user.ExistingUsername;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordResetRequest extends BaseDTO {

    @NotBlank
    @ExistingUsername
    private String username;

    @NotBlank
    private String token;

    @NotBlank
    private String password;
}
