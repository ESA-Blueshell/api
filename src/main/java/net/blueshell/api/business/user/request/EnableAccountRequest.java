package net.blueshell.api.business.user.request;

import lombok.Data;
import net.blueshell.api.util.Util;

@Data
public class EnableAccountRequest {

    private String username;

    private String token;

    public boolean isValid() {
        return !Util.isNullOrEmpty(username) && !Util.isNullOrEmpty(token);
    }
}
