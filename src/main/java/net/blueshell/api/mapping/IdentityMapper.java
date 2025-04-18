package net.blueshell.api.mapping;

import net.blueshell.api.model.User;
import net.blueshell.common.identity.Identity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class IdentityMapper {

    @Mapping(target = "roles", source = "inheritedRoles")
    public abstract Identity fromUser(User user);
}
