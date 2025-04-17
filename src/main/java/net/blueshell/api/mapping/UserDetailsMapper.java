package net.blueshell.api.mapping;

import net.blueshell.api.model.User;
import net.blueshell.common.identity.SharedUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserDetailsMapper {

    @Mapping(target = "roles", source = "inheritedRoles")
    public abstract SharedUserDetails fromUser(User user);

    public abstract User toUser(SharedUserDetails sharedUserDetails);
}
