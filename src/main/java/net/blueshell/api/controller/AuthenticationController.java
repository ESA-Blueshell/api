package net.blueshell.api.controller;

import jakarta.validation.Valid;
import net.blueshell.api.auth.JwtTokenUtil;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.controller.request.JwtRequest;
import net.blueshell.api.controller.response.JwtResponse;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController("/auth")
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    @Value("${app.jwt.expiration}")
    public Long expiration;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping
    public JwtResponse createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        User user = userService.findByUsername(authenticationRequest.getUsername());

        var token = jwtTokenUtil.generateToken(user);

        var expirationTime = System.currentTimeMillis() + expiration;

        Set<Role> roles = user.getInheritedRoles();
        return new JwtResponse(token, user.getId(), user.getUsername(), expirationTime, roles);
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
