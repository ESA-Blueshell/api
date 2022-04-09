package net.blueshell.api.business.committee;

import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class CommitteeController extends AuthorizationController {

    private final Dao<Committee> dao = new CommitteeDao();
    private final CommitteeMembershipDao membershipDao = new CommitteeMembershipDao();

    @GetMapping(value = "/committees")
    public Object getCommittees(@RequestParam(required = false) boolean fullCommittees) {
        Function<Committee, Object> fromCommittee;
        if (fullCommittees) {
            if (!hasAuthorization(Role.BOARD)) {
                return StatusCodes.FORBIDDEN;
            }
            fromCommittee = CommitteeDTO::fromCommittee;
        } else {
            fromCommittee = SimpleCommitteeDTO::fromCommittee;
        }
        return dao.list().stream()
                .map(fromCommittee)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/committees")
    public Object createCommittee(@RequestBody CommitteeDTO committeeDTO) {
        Committee committee = committeeDTO.toCommittee();
        return dao.create(committee);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/committees/{id}")
    public Object createOrUpdateCommittee(@PathVariable String id, @RequestBody CommitteeDTO committeeDTO) {
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
        newCommittee.setId(Long.parseLong(id));
        dao.update(newCommittee);

        Set<CommitteeMembership> removedMembers = new HashSet<>(oldCommittee.getMembers());
        removedMembers.removeAll(newCommittee.getMembers());
        for (CommitteeMembership membership : removedMembers) {
            membershipDao.delete(new CommitteeMembershipId(membership.getUser(), membership.getCommittee()));
        }
        return StatusCodes.OK;
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
