package net.blueshell.api.mapping;

import net.blueshell.api.common.enums.ResetType;
import net.blueshell.api.controller.request.ActivationRequest;
import net.blueshell.api.controller.request.PasswordResetRequest;
import net.blueshell.api.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class RequestMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "resetType", ignore = true)
    @Mapping(target = "password", ignore = true)
    public abstract void fromRequest(ActivationRequest request, @MappingTarget User user);

    @AfterMapping
    protected void afterFromRequest(ActivationRequest request, @MappingTarget User user) {
        if (request.getResetType() == ResetType.MEMBER_ACTIVATION) {
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setEnabled(true);
        user.setResetKey(null);
        user.setResetType(null);
        user.setResetKeyValidUntil(null);
    }

    public abstract void fromRequest(PasswordResetRequest request, @MappingTarget User user);

    @AfterMapping
    protected void afterFromRequest(PasswordResetRequest request, @MappingTarget User user) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetKey(null);
        user.setResetKeyValidUntil(null);
        user.setResetType(null);
    }
}
