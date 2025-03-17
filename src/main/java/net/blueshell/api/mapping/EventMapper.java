package net.blueshell.api.mapping;

import net.blueshell.api.base.BaseMapper;
import net.blueshell.api.dto.EventDTO;
import net.blueshell.api.mapping.committee.SimpleCommitteeMapper;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.File;
import net.blueshell.api.service.CommitteeService;
import net.blueshell.api.service.FileService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class EventMapper extends BaseMapper<Event, EventDTO> {


    @Autowired
    protected CommitteeService committeeService;
    @Autowired
    protected SimpleCommitteeMapper simpleCommitteeMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FileMapper fileMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "lastEditor", ignore = true)
    @Mapping(target = "banner", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "googleId", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    public abstract Event fromDTO(EventDTO dto);

    @AfterMapping
    protected void afterFromDTO(EventDTO dto, @MappingTarget Event event) {
        if (event.getCreator() == null) {
            event.setCreator(getPrincipal());
        }
        event.setLastEditor(getPrincipal());

        if (StringUtils.hasText(dto.getStartTime())) {
            event.setStartTime(LocalDateTime.parse(dto.getStartTime().replace(' ', 'T')));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            event.setEndTime(LocalDateTime.parse(dto.getEndTime().replace(' ', 'T')));
        }

        if (dto.getBanner() != null) {
            File banner = fileMapper.fromDTO(dto.getBanner());
            event.setBanner(banner);
        }
    }

    public abstract EventDTO toDTO(Event event);

    public void afterToDTO(Event event, @MappingTarget EventDTO dto) {
        if (event.getCommittee() != null) {
            dto.setCommittee(simpleCommitteeMapper.toDTO(event.getCommittee()));
        }
    }
}
