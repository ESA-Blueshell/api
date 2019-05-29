package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.CommitteeDao;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.model.Committee;
import net.blueshell.api.model.CommitteeMembership;
import net.blueshell.api.model.Role;
import net.blueshell.api.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CommitteeController extends AuthorizationController {

    private final Dao<Committee> dao = new CommitteeDao();

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/committees")
    public List<Committee> getCommittees() {
        return dao.list();
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
        } else {
            dao.update(com);
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/committees/{id}")
    public Object getCommitteeById(@PathVariable("id") String id) {
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
