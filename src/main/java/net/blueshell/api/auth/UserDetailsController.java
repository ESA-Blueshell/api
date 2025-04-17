package net.blueshell.api.auth;

import net.blueshell.api.mapping.UserDetailsMapper;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import net.blueshell.common.identity.SharedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-details")
public class UserDetailsController extends JWTAuthBase {

    private final UserDetailsMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public UserDetailsController(UserDetailsMapper mapper, JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, UserService userService) {
        this.mapper = mapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    public User findUser(String token) {
        try {
            if (!jwtTokenUtil.isTokenValid(token)) return null;

            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username == null) return null;

            org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(token, userDetails)) return null;

            return userService.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }


    @GetMapping
    public SharedUserDetails getUserDetails(@RequestParam String token) {
        User user = findUser(token);
        if (user == null) return null;
        return mapper.fromUser(user);
    }
}
