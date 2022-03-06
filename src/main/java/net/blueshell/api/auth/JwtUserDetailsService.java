package net.blueshell.api.auth;

import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private static final UserDao userDao = new UserDao();

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.getByUsername(username);
    }
}
