package net.blueshell.api.mapping;

import net.blueshell.dto.ContributionDTO;
import net.blueshell.api.model.Contribution;
import net.blueshell.mapper.BaseMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ContributionMapper extends BaseMapper<Contribution, ContributionDTO> {

    @Mapping(target = "id", source = "contribution.id")
    @Mapping(target = "userId", expression = "java(contribution.getUser() == null ? null : contribution.getUser().getId())")
    @Mapping(target = "contributionPeriodId", expression = "java(contribution.getContributionPeriod() == null ? null : contribution.getContributionPeriod().getId())")
    public abstract ContributionDTO toDTO(Contribution contribution);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contributionPeriod", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "paid", ignore = true)
    @Mapping(target = "remindedAt", ignore = true)
    @Mapping(target = "membership", ignore = true)
    public abstract Contribution fromDTO(ContributionDTO dto);
}
