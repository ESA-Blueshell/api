package net.blueshell.api.mapping.committee;


import net.blueshell.api.dto.committee.SimpleCommitteeDTO;
import net.blueshell.api.mapping.CommitteeMemberMapper;
import net.blueshell.api.model.Committee;
import net.blueshell.api.repository.CommitteeMemberRepository;
import net.blueshell.common.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {CommitteeMemberMapper.class}
)
public abstract class SimpleCommitteeMapper extends BaseMapper<Committee, SimpleCommitteeDTO> {

    @Autowired
    protected CommitteeMemberMapper memberMapper;
    @Autowired
    protected CommitteeMemberRepository committeeMemberRepository;

    public abstract SimpleCommitteeDTO toDTO(Committee committee);
}
