package net.blueshell.api.mapping;


import net.blueshell.api.base.BaseMapper;
import net.blueshell.api.dto.FileDTO;
import net.blueshell.api.dto.MembershipDTO;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.model.File;
import net.blueshell.api.model.Membership;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class MembershipMapper extends BaseMapper<Membership, MembershipDTO> {

    @Autowired
    private FileMapper fileMapper;

    public abstract MembershipDTO toDTO(Membership membership);

    public abstract Membership fromDTO(MembershipDTO dto);

    @AfterMapping
    protected void afterFromDTO(MembershipDTO dto, @MappingTarget Membership membership) {
        if (dto.getSignature() != null) {
            FileDTO signatureDTO = dto.getSignature();
            signatureDTO.setType(FileType.SIGNATURE);
            File signature = fileMapper.fromDTO(dto.getSignature());
            membership.setSignature(signature);
        }
    }
}