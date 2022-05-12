package net.blueshell.api.auth.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwtToken;
    private final long userId;
    private final long expiration;
    private final boolean isBoard;

    public JwtResponse(String jwtToken, long userId, long expiration, boolean isBoard) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.expiration = expiration;
        this.isBoard = isBoard;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public long getUserId() {
        return userId;
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean isBoard() {
        return isBoard;
    }
}
