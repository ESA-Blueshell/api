package net.blueshell.api.auth;

import net.blueshell.api.base.AuthorizationBase;
import net.blueshell.api.mapping.IdentityMapper;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import net.blueshell.common.identity.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
public class IdentityController extends AuthorizationBase {

    private final IdentityMapper identityMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public IdentityController(IdentityMapper identityMapper, JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, UserService userService) {
        this.identityMapper = identityMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    public User findUser(String token) {
        try {
            if (!jwtTokenUtil.isTokenValid(token)) return null;

            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username == null) return null;

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(token, userDetails)) return null;

            return userService.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }


    @GetMapping
    public Identity getIdentity(@RequestParam String token) {
        User user = findUser(token);
        if (user == null) return null;
        return identityMapper.fromUser(user);
    }
}
