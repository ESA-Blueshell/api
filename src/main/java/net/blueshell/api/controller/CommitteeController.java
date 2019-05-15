package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.CommitteeDao;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.model.Committee;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommitteeController {

    private final Dao<Committee> dao = new CommitteeDao();

    @GetMapping(value = "/committees")
    public List<Committee> getCommittees() {
        return dao.list();
    }

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
        if (com == null) {
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
        return committee;
    }

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
