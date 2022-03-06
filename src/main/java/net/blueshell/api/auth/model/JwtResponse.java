package net.blueshell.api.auth.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwtToken;
    private final long userId;

    public JwtResponse(String jwtToken, long userId) {
        this.jwtToken = jwtToken;
        this.userId = userId;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public long getUserId() {
        return userId;
    }
}
