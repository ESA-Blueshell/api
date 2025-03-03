package net.blueshell.api.mapping;

import net.blueshell.api.base.BaseMapper;
import net.blueshell.api.dto.CommitteeMemberDTO;
import net.blueshell.api.mapping.user.SimpleUserMapper;
import net.blueshell.api.model.CommitteeMember;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {SimpleUserMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CommitteeMemberMapper extends BaseMapper<CommitteeMember, CommitteeMemberDTO> {

    @Autowired
    protected SimpleUserMapper simpleUserMapper; // Field injection instead of constructor

    @Mapping(target = "role", source = "role")
    @Mapping(target = "user", ignore = true)
    public abstract CommitteeMemberDTO toDTO(CommitteeMember member);

    @AfterMapping
    protected void afterToDTO(CommitteeMember member, @MappingTarget CommitteeMemberDTO dto) {
        if (member.getUser() != null) {
            dto.setUser(simpleUserMapper.toDTO(member.getUser()));
        }
    }

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "role", source = "dto.role")
    public abstract CommitteeMember fromDTO(CommitteeMemberDTO dto);

    @AfterMapping
    protected void afterFromDTO(CommitteeMemberDTO dto, @MappingTarget CommitteeMember member) {
        if (dto.getUser() != null) {
            member.setUser(simpleUserMapper.fromDTO(dto.getUser()));
        }
    }
}