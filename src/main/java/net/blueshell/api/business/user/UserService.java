package net.blueshell.api.business.user;

import net.blueshell.api.util.Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class UserService {

    private static final int TOKEN_VALID_HOURS = 72; // 3 days

    private static final UserDao dao = new UserDao();
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String username, String password) {
        if (Util.isNullOrEmpty(username) || Util.isNullOrEmpty(password)) {
            return null;
        }

        password = passwordEncoder.encode(password);

        var user = dao.login(username, password);
        if (user != null) {
            var token = UUID.randomUUID().toString();
//            user.setToken(token);
//            user.setTokenValidUntil(Timestamp.from(LocalDateTime.now().plusHours(TOKEN_VALID_HOURS).toInstant(ZoneOffset.UTC))); // correct timezone?
            dao.update(user);
        }

//        return user == null ? null : user.getToken();
        return null;
    }

}
