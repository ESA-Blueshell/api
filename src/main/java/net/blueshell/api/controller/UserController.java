package net.blueshell.api.controller;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.Role;
import net.blueshell.api.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController extends AuthorizationController {

    private final Dao<User> dao = new UserDao();

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/users")
    public List<User> getUsers() {
        return dao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/users")
    public Object createUser(User user) {
        try {
            dao.create(user);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return user;
    }

    @PutMapping(value = "/users/{id}")
    public Object createOrUpdateUser(User user) {
        User oldUser = dao.getById(user.getId());
        if (oldUser == null && hasAuthorization(Role.BOARD)) {
            // create new user
            return createUser(user);
        } else if (isAuthedForUser(user)){
            dao.update(user);
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/users/{id}")
    public Object getUserById(
            @ApiParam(name = "Id of the user")
            @PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(user)) {
            return StatusCodes.FORBIDDEN;
        }
        return user;
    }

    @DeleteMapping(value = "/users/{id}")
    public Object deleteUserById(@PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(user)) {
            return StatusCodes.FORBIDDEN;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }


}
