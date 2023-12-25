package net.blueshell.api.business.user;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.request.EnableAccountRequest;
import net.blueshell.api.business.user.request.PasswordResetRequest;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.email.EmailModule;
import net.blueshell.api.util.TimeUtil;
import net.blueshell.api.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController extends AuthorizationController {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final int PASSWORD_RESET_KEY_LENGTH = 15;
    private static final long PASSWORD_RESET_KEY_VALID_SECONDS = 3600 * 2; // 2 hours
    private static final int INITIAL_ACCOUNT_KEY_LENGTH = 15;
    private static final long INITIAL_ACCOUNT_KEY_VALID_SECONDS = 3600 * 24 * 3; // 3 days

    @Autowired
    private EmailModule emailModule;
    @Autowired
    private UserDao dao;

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/users/members")
    public List<SimpleUserDTO> getMembers() {
        return dao.list().stream().filter(user -> user.hasRole(Role.MEMBER)).map(SimpleUserDTO::fromUser).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/users")
    public List<User> getUsers(@QueryParam("member") Boolean isMember) {
        return dao.list(isMember);
    }

    @PutMapping(value = "/createAccount")
    public Object createOrUpdateUser(@RequestBody AdvancedUserDTO userDto) {
        if (!userDto.getUsername().matches("[a-zA-Z0-9]+")) {
            return new BadRequestException("Invalid username, must only contain alphanumeric characters.");
        }

        User oldUser = dao.getById(userDto.getId());
        User userWithSameName = dao.getByUsername(userDto.getUsername());

        var user = userDto.mapToBasicUser();
        if (oldUser == null) {
            if (userWithSameName != null) {
                throw new BadRequestException("Username is already taken.");
            }
            fillInInitialFieldsAndSendMail(user);
        }
        return StatusCodes.OK;
    }

    @PutMapping(value = "/users/{id}")
    public Object updateUser(
            @ApiParam(name = "Id of the user") @PathVariable("id") String id,
            @RequestBody AdvancedUserDTO userDto) {
        var existingUser = dao.getById(Long.parseLong(id));

        if (existingUser == null || !isAuthedForUser(existingUser)) {
            return StatusCodes.NOT_FOUND;
        }
        UserModule.applyUserDtoToUser(userDto, existingUser);

        return StatusCodes.OK;
    }

    private void fillInInitialFieldsAndSendMail(User user) {
        user.setCreatedAt(TimeUtil.of(LocalDateTime.now()));
        user.setMemberSince(TimeUtil.of(LocalDateTime.of(3000, 1, 1, 0, 0)));

        // Enabled by default.
        user.setNewsletter(true);

        dao.create(user);

        // Only try doing email stuff once user has been successfully created
        user.setResetType(ResetType.INITIAL_ACCOUNT_CREATION);
        user.setResetKey(Util.getRandomCapitalString(INITIAL_ACCOUNT_KEY_LENGTH));
        user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(INITIAL_ACCOUNT_KEY_VALID_SECONDS)));
        emailModule.sendInitialKeyEmail(user);
        dao.update(user);
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

    @PostMapping(value = "/enableAccount")
    public void enableUserByEmaillink(@RequestBody EnableAccountRequest request) {
        if (request == null || !request.isValid()) {
            throw new BadRequestException("Missing username/password.");
        }

        User user = dao.getByUsername(request.getUsername());
        if (user == null) {
            throw new NotFoundException("Could not find that account.");
        }

        if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            throw new BadRequestException("Reset key has expired.");
        }

        if (!request.getToken().equals(user.getResetKey()) || user.getResetType() != ResetType.INITIAL_ACCOUNT_CREATION) {
            throw new BadRequestException("Invalid key.");
        }

        user.setResetKey(null);
        user.setResetType(null);
        user.setResetKeyValidUntil(null);
        user.setEnabled(true);

        dao.update(user);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/users/{id}/member")
    public void makeUserMember(@ApiParam(name = "Id of the user") @PathVariable("id") String id,
                               @ApiParam(name = "To enable/disable membership") @QueryParam("member") Boolean isMember) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            throw new NotFoundException("Could not find that account.");
        }

        if (isMember == null) {
            isMember = true;
        }

        if (isMember) {
            user.addRole(Role.MEMBER);
            user.setMemberSince(Timestamp.from(Instant.now()));
        } else {
            user.removeRole(Role.MEMBER);
        }

        dao.update(user);
    }

    @DeleteMapping(value = "/users/password")
    public ResponseEntity<Object> sendResetMail(@QueryParam("username") String username) {
        User user = dao.getByUsername(username);
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }

        String resetKey;
        if (user.getResetKey() == null || TimeUtil.hasExpired(user.getResetKeyValidUntil()) || user.getResetType() != ResetType.PASSWORD_RESET) {
            resetKey = Util.getRandomCapitalString(PASSWORD_RESET_KEY_LENGTH);
            user.setResetKey(resetKey);
            user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(PASSWORD_RESET_KEY_VALID_SECONDS)));
            user.setResetType(ResetType.PASSWORD_RESET);
        }

        // Send old info that is still valid (above condition is not met)
        emailModule.sendPasswordResetEmail(user);

        dao.update(user);
        return StatusCodes.OK;
    }

    @PostMapping(value = "/users/password")
    public ResponseEntity<Object> resetUserPassword(@RequestBody PasswordResetRequest resetRequest) {
        if (resetRequest == null || !resetRequest.isValid()) {
            return new ResponseEntity<>("Not every field in the reset request was filled", HttpStatus.BAD_REQUEST);
        }

        User user = dao.getByUsername(resetRequest.getUsername());
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            return new ResponseEntity<>("Reset key has expired, try resetting your password again", HttpStatus.BAD_REQUEST);
        }
        if (!resetRequest.getToken().equals(user.getResetKey()) || user.getResetType() != ResetType.PASSWORD_RESET) {
            return new ResponseEntity<>("Invalid reset key", HttpStatus.BAD_REQUEST);
        }

        //Set the new password
        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));

        //Remove reset info
        user.setResetKey(null);
        user.setResetKeyValidUntil(null);
        user.setResetType(null);

        dao.update(user);
        return StatusCodes.OK;
    }


}
