package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.SponsorDao;
import net.blueshell.api.model.Sponsor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SponsorController {
    private final Dao<Sponsor> dao = new SponsorDao();

    @GetMapping(value = "/sponsors")
    public List<Sponsor> getSponsors() {
        return dao.list();
    }

    @PostMapping(value = "/sponsors")
    public Object createSponsor(Sponsor sponsor) {
        try {
            dao.create(sponsor);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return sponsor;
    }

    @PutMapping(value = "/sponsors/{id}")
    public Object createOrUpdateSponsor(Sponsor sponsor) {
        Sponsor oldSponsor = dao.getById(sponsor.getId());
        if (oldSponsor == null) {
            // create new sponsor
            return createSponsor(sponsor);
        } else {
            dao.update(sponsor);
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/sponsors/{id}")
    public Object getSponsorById(@PathVariable("id") String id) {
        Sponsor sponsor = dao.getById(Long.parseLong(id));
        if (sponsor == null) {
            return StatusCodes.NOT_FOUND;
        }
        return sponsor;
    }

    @DeleteMapping(value = "/sponsors/{id}")
    public Object deleteSponsorById(@PathVariable("id") String id) {
        Sponsor sponsor = dao.getById(Long.parseLong(id));
        if (sponsor == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }
}
