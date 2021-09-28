package net.blueshell.api.business.billable;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Billable management", description = "Operations with everything billable related")
@RestController
public class BillableController extends AuthorizationController {

    private final Dao<Billable> dao = new BillableDao();

    @ApiOperation(value = "Get a list of all billables", response = List.class)
    @PreAuthorize("hasAuthority('TREASURER')")
    @GetMapping(value = "/billables")
    public List<Billable> getBillables() {
        return dao.list();
    }

    @ApiOperation(value = "Create a new billable", response = Billable.class)
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

    @ApiOperation(value = "Creates or updates a billable, given it's id", response = Billable.class)
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

    @ApiOperation(value = "Gets a billable by id.", response = Billable.class)
    @GetMapping(value = "/billables/{id}")
    public Object getBillableById(
            @ApiParam(name = "Id of the billable")
            @PathVariable("id") String id) {
        Billable billable = dao.getById(Long.parseLong(id));
        if (billable == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(billable.getSource())) {
            return StatusCodes.FORBIDDEN;
        }
        return billable;
    }

    @ApiOperation(value = "Deletes a billable by id.")
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
