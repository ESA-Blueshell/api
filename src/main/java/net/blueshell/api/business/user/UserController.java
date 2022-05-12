package net.blueshell.api.business.user;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.request.PasswordResetRequest;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.email.EmailModule;
import net.blueshell.api.util.TimeUtil;
import net.blueshell.api.util.Util;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.List;

@RestController
public class UserController extends AuthorizationController {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final int PASSWORD_RESET_KEY_LENGTH = 15;
    private static final long PASSWORD_RESET_KEY_VALID_SECONDS = 3600 * 2; // 2 hours

    private final UserDao dao = new UserDao();

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

    @PutMapping(value = "/users")
    public Object createOrUpdateUser(User user) {
        User oldUser = dao.getById(user.getId());
        User userWithSameName = dao.getByUsername(user.getUsername());
        if (oldUser == null) {
            if (userWithSameName != null) {
                return new BadRequestException("Username is already taken.");
            }

            // create new user
            return createUser(user);
        } else if (isAuthedForUser(user)){
            dao.update(user);
        }
        return StatusCodes.OK;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PostMapping(value = "/users/{id}/enable")
    public void enableUserByEmaillink(@PathVariable("id") String id, @QueryParam("token") String token) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            throw new NotFoundException();
        }

        if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            throw new BadRequestException("Reset key has expired.");
        }

        if (!token.equals(user.getResetKey()) || user.getResetType() != ResetType.INITIAL_ACCOUNT_CREATION) {
            throw new BadRequestException("Invalid key.");
        }

        user.setEnabled(true);

        dao.update(user);
    }

    @DeleteMapping(value = "/users/{id}/password")
    public void enableUserByEmaillink(@PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            throw new NotFoundException();
        }

        String resetKey;
        if (user.getResetKey() == null || TimeUtil.hasExpired(user.getResetKeyValidUntil()) || user.getResetType() != ResetType.PASSWORD_RESET) {
            resetKey = Util.getRandomCapitalString(PASSWORD_RESET_KEY_LENGTH);
            user.setResetKey(resetKey);
            user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(PASSWORD_RESET_KEY_VALID_SECONDS)));
            user.setResetType(ResetType.PASSWORD_RESET);
        }

        // Send old info that is still valid (above condition is not met)
        EmailModule.sendPasswordResetEmail(user);

        dao.update(user);
    }

    @PostMapping(value = "/users/{id}/password")
    public void enableUserByEmaillink(@PathVariable("id") String id, @QueryParam("token") String token, PasswordResetRequest resetRequest) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            throw new NotFoundException();
        }

        if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            throw new BadRequestException("Reset key has expired.");
        }

        if (!token.equals(user.getResetKey()) || user.getResetType() != ResetType.PASSWORD_RESET) {
            throw new BadRequestException("Invalid key.");
        }

        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));

        dao.update(user);
    }


}
