package net.blueshell.api.mapping;


import net.blueshell.api.base.BaseMapper;
import net.blueshell.api.dto.FileDTO;
import net.blueshell.api.dto.MemberDTO;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.model.File;
import net.blueshell.api.model.Member;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class MemberMapper extends BaseMapper<Member, MemberDTO> {

    @Autowired
    private FileMapper fileMapper;

    public abstract MemberDTO toDTO(Member member);

    public abstract Member fromDTO(MemberDTO dto);

    @AfterMapping
    protected void afterFromDTO(MemberDTO dto, @MappingTarget Member member) {
        if (dto.getSignature() != null) {
            FileDTO signatureDTO = dto.getSignature();
            signatureDTO.setFileType(FileType.SIGNATURE);
            File signature = fileMapper.fromDTO(dto.getSignature());
            member.setSignature(signature);
        }
        if (member.getStartDate() == null) {
            member.setStartDate(LocalDate.now());
        }
    }
}