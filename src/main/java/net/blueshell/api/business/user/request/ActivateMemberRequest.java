package net.blueshell.api.business.user.request;

import lombok.Data;
import net.blueshell.api.util.Util;

@Data
public class ActivateMemberRequest {

    private String username;

    private String token;

    private String password;

    public boolean isValid() {
        return !Util.isNullOrEmpty(username) && !Util.isNullOrEmpty(token) && !Util.isNullOrEmpty(password);
    }
}
