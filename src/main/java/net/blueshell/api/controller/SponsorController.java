package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.SponsorDao;
import net.blueshell.api.model.Sponsor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SponsorController {
    private final Dao<Sponsor> dao = new SponsorDao();

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/sponsors")
    public List<Sponsor> getSponsors() {
        return dao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
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

    @PreAuthorize("hasAuthority('BOARD')")
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

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/sponsors/{id}")
    public Object getSponsorById(@PathVariable("id") String id) {
        Sponsor sponsor = dao.getById(Long.parseLong(id));
        if (sponsor == null) {
            return StatusCodes.NOT_FOUND;
        }
        return sponsor;
    }

    @PreAuthorize("hasAuthority('BOARD')")
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
