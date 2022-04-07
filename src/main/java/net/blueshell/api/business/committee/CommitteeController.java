package net.blueshell.api.business.committee;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class CommitteeController extends AuthorizationController {

    private final Dao<Committee> dao = new CommitteeDao();

    @GetMapping(value = "/committees")
    public Object getCommittees() {
        User authedUser = getPrincipal();
        if (hasAuthorization(Role.MEMBER)) {
            return StatusCodes.FORBIDDEN;
        }
        Set<Long> authedUserCommitteeIds = authedUser.getCommitteeIds();
        return dao.list().stream()
                .filter(committee -> authedUserCommitteeIds.contains(committee.getId()))
                .map(CommitteeDTO::fromCommittee)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/committees")
    public Object createCommittee(CommitteeDTO committeeDTO) {
        Committee committee = committeeDTO.toCommittee();
        return dao.create(committee);
    }

    @PutMapping(value = "/committees/{id}")
    public Object createOrUpdateCommittee(@PathVariable String id, CommitteeDTO committeeDTO) {
        Committee oldCommittee = dao.getById(Long.parseLong(id));
        Committee newCommittee = committeeDTO.toCommittee();
        User authedUser = getPrincipal();
        // Check if committee exists
        if (oldCommittee == null) {
            return StatusCodes.NOT_FOUND;
        }
        // Check is user is part of committee or is board
        if (!oldCommittee.hasMember(authedUser) && !hasAuthorization(Role.BOARD)) {
            return StatusCodes.NOT_FOUND;
        }
        dao.update(newCommittee);
        return StatusCodes.OK;
    }

    @GetMapping(value = "/committees/{id}")
    public Object getCommitteeById(
            @ApiParam(name = "Id of the committee")
            @PathVariable("id") String id) {
        Committee committee = dao.getById(Long.parseLong(id));
        if (committee == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!committee.hasMember(getAuthorizedUsername()) && !hasAuthorization(Role.BOARD)) {
            return StatusCodes.NOT_FOUND;
        }
        return CommitteeDTO.fromCommittee(committee);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/committees/{id}")
    public Object deleteCommitteeById(@PathVariable("id") String id) {
        Committee committee = dao.getById(Long.parseLong(id));
        if (committee == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }
}
