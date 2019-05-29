package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.BillableDao;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.model.Billable;
import net.blueshell.api.model.Role;
import net.blueshell.api.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BillableController extends AuthorizationController {

    private final Dao<Billable> dao = new BillableDao();

    @PreAuthorize("hasAuthority('TREASURER')")
    @GetMapping(value = "/billables")
    public List<Billable> getBillables() {
        return dao.list();
    }

    @PreAuthorize("hasAuthority('TREASURER')")
    @PostMapping(value = "/billables")
    public Object createBillable(Billable billable) {
        try {
            return dao.create(billable);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
    }

    @PutMapping(value = "/billables/{id}")
    public Object createOrUpdateBillable(Billable billable) {
        User user = billable.getSource();
        Billable bil = dao.getById(billable.getId());
        if (bil == null && hasAuthorization(Role.TREASURER)) {
            // create new billable
            return createBillable(billable);
        } else if (isAuthedForUser(user)){
            dao.update(bil);
        } else {
            return StatusCodes.FORBIDDEN;
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/billables/{id}")
    public Object getBillableById(@PathVariable("id") String id) {
        Billable billable = dao.getById(Long.parseLong(id));
        if (billable == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(billable.getSource())) {
            return StatusCodes.FORBIDDEN;
        }
        return billable;
    }

    @PreAuthorize("hasAuthority('TREASURER')")
    @DeleteMapping(value = "/billables/{id}")
    public Object deleteBillableById(@PathVariable("id") String id) {
        Billable billable = dao.getById(Long.parseLong(id));
        if (billable == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }
}
