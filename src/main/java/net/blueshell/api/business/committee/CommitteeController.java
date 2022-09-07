package net.blueshell.api.business.committee;

import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class CommitteeController extends AuthorizationController {

    private final Dao<Committee> dao = new CommitteeDao();
    private final Dao<User> userDao = new UserDao();
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
    public Committee createCommittee(@RequestBody CommitteeDTO committeeDTO) {
        Committee committee = committeeDTO.toCommittee();
        var committeeObj = dao.create(committee);

        for (var member : committeeObj.getMembers()) {
            var user = member.getUser();
            user.addRole(Role.COMMITTEE);
            userDao.update(user);
        }

        return committee;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/committees/{id}")
    public void createOrUpdateCommittee(@PathVariable String id, @RequestBody CommitteeDTO committeeDTO) {
        Committee oldCommittee = dao.getById(Long.parseLong(id));
        Committee newCommittee = committeeDTO.toCommittee();
        User authedUser = getPrincipal();
        // Check if committee exists
        if (oldCommittee == null) {
            throw new NotFoundException();
        }
        // Check is user is part of committee or is board
        if (!oldCommittee.hasMember(authedUser) && !hasAuthorization(Role.BOARD)) {
            throw new NotFoundException();
        }
        newCommittee.setId(Long.parseLong(id));

        // Remove old members (members are re-added automatically by the dao.update() call
        for (CommitteeMembership membership : oldCommittee.getMembers()) {
            var user = membership.getUser();
            membershipDao.delete(new CommitteeMembershipId(user, membership.getCommittee()));
            if (user.getCommitteeMemberships().isEmpty()) {
                user.getRoles().remove(Role.COMMITTEE);
                userDao.update(user);
            }
        }
        for (CommitteeMembership membership : newCommittee.getMembers()) {
            var user = membership.getUser();
            user.addRole(Role.COMMITTEE); // No double roles since it's a set
            userDao.update(user);
        }
        dao.update(newCommittee);
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
