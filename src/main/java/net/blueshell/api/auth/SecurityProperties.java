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

        private Resource keyStore = new ClassPathResource("keystore.jks");
        private Resource publicKey = new ClassPathResource("public.txt");
        private String keyStorePassword = "blueshell";
        private String keyPairAlias = "tim";
        private String keyPairPassword = "blueshell";

        public Resource getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(Resource keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyPairAlias() {
            return keyPairAlias;
        }

        public void setKeyPairAlias(String keyPairAlias) {
            this.keyPairAlias = keyPairAlias;
        }

        public String getKeyPairPassword() {
            return keyPairPassword;
        }

        public void setKeyPairPassword(String keyPairPassword) {
            this.keyPairPassword = keyPairPassword;
        }

        public Resource getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(Resource publicKey) {
            this.publicKey = publicKey;
        }
    }
}
