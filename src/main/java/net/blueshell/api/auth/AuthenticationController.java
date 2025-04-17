package net.blueshell.api.auth;

import jakarta.validation.Valid;
import net.blueshell.api.controller.request.JwtRequest;
import net.blueshell.api.controller.response.JwtResponse;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
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
}
