package net.blueshell.api.mapping;



import net.blueshell.api.dto.FileDTO;
import net.blueshell.api.dto.MembershipDTO;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.mapping.user.SimpleUserMapper;
import net.blueshell.api.model.File;
import net.blueshell.api.model.Membership;
import net.blueshell.api.service.UserService;
import net.blueshell.common.mapper.BaseMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class MembershipMapper extends BaseMapper<Membership, MembershipDTO> {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserService userService;

    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    public abstract MembershipDTO toDTO(Membership membership);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contributions", ignore = true)
    @Mapping(target = "incasso", ignore = true)
    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    public abstract Membership fromDTO(MembershipDTO dto);

    @AfterMapping
    protected void afterFromDTO(MembershipDTO dto, @MappingTarget Membership membership) {
        if (dto.getSignature() != null) {
            FileDTO signatureDTO = dto.getSignature();
            signatureDTO.setFileType(FileType.SIGNATURE);
            File signature = fileMapper.fromDTO(dto.getSignature());
            membership.setSignature(signature);
        }
        if (membership.getStartDate() == null) {
            membership.setStartDate(LocalDate.now());
        }
        if (dto.getUserId() != null) {
            membership.setUser(userService.findById(dto.getUserId()));
        }
    }
}