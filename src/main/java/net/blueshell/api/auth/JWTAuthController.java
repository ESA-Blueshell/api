package net.blueshell.api.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.blueshell.api.controller.request.JwtRequest;
import net.blueshell.api.controller.response.JwtResponse;
import net.blueshell.api.mapping.IdentityMapper;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import net.blueshell.common.identity.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class JWTAuthController extends JWTAuthBase{

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final IdentityMapper mapper;


    @Value("${app.jwt.expiration}")
    private Long expiration;

    public JWTAuthController(
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            UserService userService, IdentityMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public JwtResponse createAuthenticationToken(
            @Valid @RequestBody JwtRequest authenticationRequest) {

        authenticate(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
        );

        User user = userService.findByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(user);
        long expirationTime = System.currentTimeMillis() + expiration;

        return new JwtResponse(token,
                user.getId(),
                user.getUsername(),
                expirationTime,
                user.getRoleStrings());
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
    }


    @GetMapping("/identity")
    public Identity getIdentity(@RequestParam(required = false) String token) {
        User user = getPrincipal();
        if (user == null) return null;
        return mapper.fromUser(user);
    }
}
