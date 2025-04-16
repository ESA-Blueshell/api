package net.blueshell.api.controller;

import net.blueshell.api.base.AuthorizationBase;
import net.blueshell.api.mapping.IdentityMapper;
import net.blueshell.api.model.User;
import net.blueshell.common.identity.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/identity")
public class IdentityController extends AuthorizationBase {

    private final IdentityMapper identityMapper;

    @Autowired
    public IdentityController(IdentityMapper identityMapper) {
        this.identityMapper = identityMapper;
    }

    @GetMapping
    public Identity getIdentity() {
        User principal = getPrincipal();
        if (principal == null) return null;
        return identityMapper.fromUser(principal);
    }
}
