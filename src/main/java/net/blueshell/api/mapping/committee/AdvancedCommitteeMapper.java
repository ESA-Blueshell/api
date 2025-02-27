package net.blueshell.api.mapping.committee;

import net.blueshell.api.base.BaseMapper;
import net.blueshell.api.dto.committee.AdvancedCommitteeDTO;
import net.blueshell.api.mapping.CommitteeMemberMapper;
import net.blueshell.api.model.Committee;
import net.blueshell.api.repository.CommitteeMemberRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;

@Mapper(
        componentModel = "spring",
        uses = {CommitteeMemberMapper.class}
)
public abstract class AdvancedCommitteeMapper extends BaseMapper<Committee, AdvancedCommitteeDTO> {

    @Autowired
    protected CommitteeMemberMapper memberMapper;
    @Autowired
    protected CommitteeMemberRepository committeeMemberRepository;


    @Mapping(target = "committeeMembers", ignore = true)
    public abstract Committee fromDTO(AdvancedCommitteeDTO dto);

    @AfterMapping
    protected void afterFromDTO(AdvancedCommitteeDTO dto,
                                @MappingTarget Committee committee) {
        if (dto.getCommitteeMembers() != null) {
            // Option 1
//            HashSet<CommitteeMember> newMembers = new HashSet<>(memberMapper.fromDTOList(dto.getMembers(), committee));
//            HashSet<CommitteeMember> oldMembers = new HashSet<>(committee.getMembers());
//
//            HashSet<CommitteeMember> membersToRemove = new HashSet<>(oldMembers);
//            membersToRemove.removeAll(newMembers);
//
//            HashSet<CommitteeMember> membersToAdd = new HashSet<>(newMembers);
//            membersToAdd.removeAll(oldMembers);
//
//            committeeMemberRepository.deleteAll(membersToRemove);
//            committeeMemberRepository.saveAll(membersToAdd);

            // Option 2

            committee.setCommitteeMembers(
                    new HashSet<>(memberMapper.fromDTOs(dto.getCommitteeMembers()))
            );
        }
    }

    @Mapping(target = "committeeMembers", ignore = true)
    public abstract AdvancedCommitteeDTO toDTO(Committee committee);

    @AfterMapping
    protected void afterToDTO(Committee committee,
                              @MappingTarget AdvancedCommitteeDTO dto) {
        if (committee.getCommitteeMembers() != null) {
            dto.setCommitteeMembers(
                    memberMapper.toDTOs(new ArrayList<>(committee.getCommitteeMembers()))
            );
        }
    }
}
