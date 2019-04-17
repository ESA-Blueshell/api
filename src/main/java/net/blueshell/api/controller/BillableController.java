package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.BillableDao;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.model.Billable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BillableController {

    private final Dao<Billable> dao = new BillableDao();

    @GetMapping(value = "/billables")
    public List<Billable> getBillables() {
        return dao.list();
    }

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
        Billable bil = dao.getById(billable.getId());
        if (bil == null) {
            // create new billable
            return createBillable(billable);
        } else {
            dao.update(bil);
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/billables/{id}")
    public Object getBillableById(@PathVariable("id") String id) {
        Billable billable = dao.getById(Long.parseLong(id));
        if (billable == null) {
            return StatusCodes.NOT_FOUND;
        }
        return billable;
    }

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
