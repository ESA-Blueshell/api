package net.blueshell.api.business.user.request;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String newPassword;

}
