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
        if (authedUser == null) {
            return StatusCodes.FORBIDDEN;
        }
        Set<Long> authedUserCommitteeIds = authedUser.getCommitteeIds();
        return dao.list().stream().filter(committee -> authedUserCommitteeIds.contains(committee.getId())).collect(Collectors.toList());

    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/committees")
    public Object createCommittee(Committee committee) {
        try {
            return dao.create(committee);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
    }

    @PutMapping(value = "/committees/{id}")
    public Object createOrUpdateCommittee(Committee committee) {
        Committee com = dao.getById(committee.getId());
        if (com == null && hasAuthorization(Role.BOARD)) {
            // create new committee
            return createCommittee(committee);
        } else if (com != null && com.hasMember(getAuthorizedUsername())) {
            dao.update(com);
        } else {
            return StatusCodes.NOT_FOUND;
        }
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
        if (!committee.hasMember(getAuthorizedUsername())) {
            return StatusCodes.FORBIDDEN;
        }
        return committee;
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
