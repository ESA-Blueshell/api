package net.blueshell.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * https://www.javacodegeeks.com/2019/03/centralized_-authorization_-oauth2_jwt.html
 */
@ConfigurationProperties("security")
public class SecurityProperties {
    private JwtProperties jwt = new JwtProperties();

    public JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }

    public static class JwtProperties {

    }
}
