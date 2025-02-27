package net.blueshell.api.controller.request;

import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.base.DTO;

import java.io.Serializable;

@Setter
@Getter
public class JwtRequest extends DTO implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;

    public JwtRequest() {
    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
