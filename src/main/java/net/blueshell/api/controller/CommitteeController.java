package net.blueshell.api.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import net.blueshell.api.base.AdvancedController;
import net.blueshell.api.base.DTO;
import net.blueshell.api.common.constants.StatusCodes;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.dto.committee.AdvancedCommitteeDTO;
import net.blueshell.api.mapping.committee.AdvancedCommitteeMapper;
import net.blueshell.api.mapping.committee.SimpleCommitteeMapper;
import net.blueshell.api.model.Committee;
import net.blueshell.api.model.User;
import net.blueshell.api.service.CommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RestController
@RequestMapping("/committees")
public class CommitteeController extends AdvancedController<CommitteeService, AdvancedCommitteeMapper, SimpleCommitteeMapper> {

    @Autowired
    public CommitteeController(CommitteeService service, AdvancedCommitteeMapper advancedCommitteeMapper, SimpleCommitteeMapper simpleCommitteeMapper) {
        super(service, advancedCommitteeMapper, simpleCommitteeMapper);
    }

    @GetMapping()
    public List<? extends DTO> getCommittees(@RequestParam(required = false) boolean isMember) {
        if (getPrincipal() != null && getPrincipal().hasRole(Role.BOARD)) {
            return advancedMapper.toDTOs(service.findAll());
        } else if (isMember) {
            return advancedMapper.toDTOs(service.findAllById(new ArrayList<>(getPrincipal().getCommitteeIds())));
        }

        return simpleMapper.toDTOs(service.findAll());
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping()
    public DTO createCommittee(@Valid @RequestBody AdvancedCommitteeDTO advancedCommitteeDTO) {
        Committee committee = advancedMapper.fromDTO(advancedCommitteeDTO);
        service.createCommittee(committee);
        return advancedMapper.toDTO(committee);
    }

    @PreAuthorize("hasPermission()")
    @PutMapping(value = "/{committeeId}")
    public DTO createOrUpdateCommittee(@PathVariable("committeeId") Long committeeId, @Valid @RequestBody AdvancedCommitteeDTO advancedCommitteeDTO) {
        Committee oldCommittee = service.findById(committeeId);

        if (!oldCommittee.hasMember(getPrincipal()) && !hasAuthority(Role.BOARD)) {
            throw new NotFoundException();
        }

        Committee newCommittee = advancedMapper.fromDTO(advancedCommitteeDTO);
        newCommittee.setId(committeeId);
        service.update(newCommittee);
        return advancedMapper.toDTO(newCommittee);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/{id}")
    public Object deleteCommitteeById(@PathVariable("id") Long id) {
        service.deleteById(id);
        return StatusCodes.OK;
    }
}
