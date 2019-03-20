package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final Dao<User> dao = new UserDao();

    @GetMapping(value = "/users")
    public List<User> getPictures() {
        return dao.list();
    }

    @PostMapping(value = "/users")
    public Object createUser(User user) {
        try {
            dao.create(user);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return StatusCodes.CREATED;
    }

    @GetMapping(value = "/users/{id}")
    public Object getUserById(@PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        return user;
    }

    @DeleteMapping(value = "/users/{id}")
    public Object deleteUserById(@PathVariable("id") String id) {
        User pic = dao.getById(Long.parseLong(id));
        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

}
