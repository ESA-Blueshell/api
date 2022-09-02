package net.blueshell.api.auth.model;

import net.blueshell.api.business.user.Role;

import java.io.Serializable;
import java.util.Set;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwtToken;
    private final long userId;
    private final long expiration;
    private Set<Role> roles;

    public JwtResponse(String jwtToken, long userId, long expiration, Set<Role> roles) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.expiration = expiration;
        this.roles = roles;
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

    public Set<Role> getRoles() {
        return roles;
    }
}
