package net.blueshell.api.mapping;

import net.blueshell.api.model.User;
import net.blueshell.common.identity.Identity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class IdentityMapper {

    public abstract Identity fromUser(User user);

    public abstract User toUser(Identity identity);
}
