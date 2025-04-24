package net.blueshell.api.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import net.blueshell.db.AdvancedController;
import net.blueshell.dto.BaseDTO;
import net.blueshell.enums.Role;
import net.blueshell.dto.AdvancedCommitteeDTO;
import net.blueshell.api.mapping.committee.AdvancedCommitteeMapper;
import net.blueshell.api.mapping.committee.SimpleCommitteeMapper;
import net.blueshell.api.model.Committee;
import net.blueshell.api.service.CommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommitteeController extends AdvancedController<CommitteeService, AdvancedCommitteeMapper, SimpleCommitteeMapper> {

    @Autowired
    public CommitteeController(CommitteeService service, AdvancedCommitteeMapper advancedCommitteeMapper, SimpleCommitteeMapper simpleCommitteeMapper) {
        super(service, advancedCommitteeMapper, simpleCommitteeMapper);
    }

    @GetMapping("/committees")
    public List<? extends BaseDTO> getCommittees(@RequestParam(required = false) boolean isMember) {
        if (getPrincipal() != null && hasAuthority(Role.BOARD)) {
            return advancedMapper.toDTOs(service.findAll());
        } else if (isMember) {
            List<Committee> committees = service.findALlByUserId(getPrincipal().getId());
            return advancedMapper.toDTOs(committees);
        }

        return simpleMapper.toDTOs(service.findAll());
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping("/committees")
    public AdvancedCommitteeDTO createCommittee(@Valid @RequestBody AdvancedCommitteeDTO advancedCommitteeDTO) {
        Committee committee = advancedMapper.fromDTO(advancedCommitteeDTO);
        service.createCommittee(committee);
        return advancedMapper.toDTO(committee);
    }

    @PreAuthorize("hasPermission(#committeeId, 'Committee', 'write')")
    @PutMapping(value = "/committees/{committeeId}")
    public BaseDTO updateCommittee(
            @PathVariable("committeeId") Long committeeId,
            @Valid @RequestBody AdvancedCommitteeDTO advancedCommitteeDTO) {
        Committee oldCommittee = service.findById(committeeId);

        if (!oldCommittee.hasMember(getPrincipal()) && !hasAuthority(Role.BOARD)) {
            throw new NotFoundException();
        }

        advancedCommitteeDTO.setId(committeeId);
        Committee newCommittee = advancedMapper.fromDTO(advancedCommitteeDTO);
        service.update(newCommittee);
        return advancedMapper.toDTO(newCommittee);
    }

    @PreAuthorize("hasPermission(#committeeId, 'Committee', 'delete')")
    @DeleteMapping(value = "/committees/{committeeId}")
    public void deleteCommitteeById(@PathVariable("committeeId") Long committeeId) {
        service.deleteById(committeeId);
    }
}
