package net.blueshell.api.mapping.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.blueshell.api.dto.CommitteeMemberDTO;
import net.blueshell.api.dto.user.AdvancedUserDTO;
import net.blueshell.api.dto.user.SimpleUserDTO;
import net.blueshell.api.model.User;
import net.blueshell.common.mapper.BaseMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class SimpleUserMapper extends BaseMapper<User, SimpleUserDTO> {
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    public abstract SimpleUserDTO toDTO(User user);

    @InheritInverseConfiguration(name = "toDTO")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "committeeMembers", ignore = true)
    @Mapping(target = "contributions", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "resetKey", ignore = true)
    @Mapping(target = "resetKeyValidUntil", ignore = true)
    @Mapping(target = "resetType", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract User fromDTO(SimpleUserDTO dto);
}
