package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.RegistrationDao;
import net.blueshell.api.model.Registration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {
    private final Dao<Registration> dao = new RegistrationDao();

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/registrations")
    public List<Registration> getRegistrations() {
        return dao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/registrations")
    public Object createRegistration(Registration registration) {
        try {
            dao.create(registration);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return registration;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/registrations/{id}")
    public Object createOrUpdateRegistration(Registration registration) {
        Registration oldRegistration = dao.getById(registration.getId());
        if (oldRegistration == null) {
            // create new registration
            return createRegistration(registration);
        } else {
            dao.update(registration);
        }
        return StatusCodes.OK;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/registrations/{id}")
    public Object getRegistrationById(@PathVariable("id") String id) {
        Registration registration = dao.getById(Long.parseLong(id));
        if (registration == null) {
            return StatusCodes.NOT_FOUND;
        }
        return registration;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/registrations/{id}")
    public Object deleteRegistrationById(@PathVariable("id") String id) {
        Registration registration = dao.getById(Long.parseLong(id));
        if (registration == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }
}
