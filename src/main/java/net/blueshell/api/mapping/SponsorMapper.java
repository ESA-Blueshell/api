package net.blueshell.api.mapping;

import net.blueshell.common.dto.SponsorDTO;
import net.blueshell.api.model.Sponsor;
import net.blueshell.common.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SponsorMapper extends BaseMapper<Sponsor, SponsorDTO> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "picture", ignore = true)
    public abstract Sponsor fromDTO(SponsorDTO dto);

    public abstract SponsorDTO toDTO(Sponsor sponsor);
}
