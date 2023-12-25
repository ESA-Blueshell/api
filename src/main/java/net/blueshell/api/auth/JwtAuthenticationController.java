package net.blueshell.api.auth;

import net.blueshell.api.auth.model.JwtRequest;
import net.blueshell.api.auth.model.JwtResponse;
import net.blueshell.api.business.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        var user = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        var token = jwtTokenUtil.generateToken(user);

        var expiration = System.currentTimeMillis() + JwtTokenUtil.JWT_TOKEN_VALIDITY;

        Set<Role> roles = user.getRoles();
        //Add all inherited roles
        roles.addAll(roles.stream().flatMap(role -> role.getAllInheritedRoles().stream()).collect(Collectors.toList()));
        return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getUsername(), expiration, roles));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("WHAT???", e);
        }
    }

}
