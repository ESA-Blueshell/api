package net.blueshell.api.business.user;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.request.ActivateUserRequest;
import net.blueshell.api.business.user.request.PasswordResetRequest;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.service.BrevoContactService;
import net.blueshell.api.service.BrevoEmailService;
import net.blueshell.api.storage.StorageService;
import net.blueshell.api.util.TimeUtil;
import net.blueshell.api.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController extends AuthorizationController {

    private static final int ACTIVATION_KEY_LENGTH = 15;
    private static final long USER_ACTIVATION_VALID_SECONDS = 3600 * 24 * 3; // 3 days
    private static final int PASSWORD_RESET_KEY_LENGTH = 15;
    private static final long PASSWORD_RESET_VALID_SECONDS = 3600 * 2; // 2 hours

    @Autowired
    private BrevoEmailService brevoEmailService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BrevoContactService brevoContactService;

    private final StorageService storageService;

    public UserController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/users")
    public ResponseEntity<Object> create(@RequestBody AdvancedUserDTO dto) {
        if (userDao.getByEmail(dto.getEmail()) != null) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        } else if (userDao.getByPhoneNumber(dto.getPhoneNumber()) != null) {
            return new ResponseEntity<>("Phone number is already taken.", HttpStatus.BAD_REQUEST);
        } else if (userDao.getByUsername(dto.getUsername()) != null) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        UserModule.applyCreationFields(user, dto);
        UserModule.applyAdminFields(user, dto);
        UserModule.applyEditableFields(user, dto);
        UserModule.storeSignature(user, dto, storageService);

        try {
            brevoContactService.createOrUpdateContact(user);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(e.getResponseBody());
        }

        sendUserActivationEmail(user);
        userDao.create(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/users/password/reset")
    public ResponseEntity<Object> resetPassword(@RequestParam("username") String username) {
        User user = userDao.getByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        user.setResetKey(Util.getRandomCapitalString(PASSWORD_RESET_KEY_LENGTH));
        user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(PASSWORD_RESET_VALID_SECONDS)));
        user.setResetType(ResetType.PASSWORD_RESET);
        brevoEmailService.sendPasswordResetEmail(user);

        userDao.update(user);
        return ResponseEntity.ok().build();
    }

    private void sendUserActivationEmail(User user) {
        user.setResetKey(Util.getRandomCapitalString(ACTIVATION_KEY_LENGTH));
        user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(USER_ACTIVATION_VALID_SECONDS)));
        user.setResetType(ResetType.USER_ACTIVATION);
        brevoEmailService.sendUserActivationEmail(user);
    }


    @PostMapping(value = "/users/activate")
    public ResponseEntity<Object> activate(@RequestBody ActivateUserRequest request) {
        if (request == null || !request.isValid()) {
            return new ResponseEntity<>("Missing token or required fields.", HttpStatus.BAD_REQUEST);
        }

        User user = userDao.getByUsername(request.getUsername());
        if (user == null) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.NOT_FOUND);
        } else if (user.getResetType() != ResetType.USER_ACTIVATION) {
            return new ResponseEntity<>("Invalid reset type.", HttpStatus.BAD_REQUEST);
        } else if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            return new ResponseEntity<>("Activation key has expired.", HttpStatus.BAD_REQUEST);
        } else if (userDao.getByUsername(request.getUsername()) == null) {
            return new ResponseEntity<>("User doesn't exist.", HttpStatus.BAD_REQUEST);
        }

        UserModule.activateAccount(user);
        userDao.update(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/users/password")
    public ResponseEntity<Object> setPassword(@RequestBody PasswordResetRequest request) {
        if (request == null || !request.isValid()) {
            return new ResponseEntity<>("All fields in the reset request are required.", HttpStatus.BAD_REQUEST);
        }

        User user = userDao.getByUsername(request.getUsername());
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        } else if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            return new ResponseEntity<>("Reset key has expired. Try resetting your password again.", HttpStatus.BAD_REQUEST);
        } else if (!request.getToken().equals(user.getResetKey()) || user.getResetType() != ResetType.PASSWORD_RESET) {
            return new ResponseEntity<>("Invalid reset key.", HttpStatus.BAD_REQUEST);
        }

        UserModule.setPassword(user, request);
        userDao.update(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/users")
    @PreAuthorize("hasAuthority('BOARD')")
    public List<User> getAll(@QueryParam("isMember") Boolean isMember) {
        return userDao.list(isMember);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getById(@ApiParam(name = "Id of the user") @PathVariable("id") Long id) {
        User user = userDao.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!isAuthedForUser(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/users/{id}")
    public ResponseEntity<Object> updateUser(@ApiParam(name = "Id of the user") @PathVariable("id") Long id,
                                             @RequestBody AdvancedUserDTO userDto) {
        User user = userDao.getById(id);
        if (user == null || !isAuthedForUser(user)) {
            return new ResponseEntity<>("User not found or unauthorized.", HttpStatus.BAD_REQUEST);
        }

        User userWithSamePhone = userDao.getByPhoneNumber(userDto.getPhoneNumber());
        if (userWithSamePhone != null && user.getId() != userWithSamePhone.getId()) {
            return new ResponseEntity<>("Phone number is already taken.", HttpStatus.BAD_REQUEST);
        }

        if (getPrincipal().hasRole(Role.ADMIN) || getPrincipal().hasRole(Role.SECRETARY)) {
            UserModule.applyAdminFields(user, userDto);
        }

        UserModule.applyEditableFields(user, userDto);
        try {
            brevoContactService.createOrUpdateContact(user);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(e.getResponseBody());
        }
        userDao.update(user);

        return new ResponseEntity<>(userDao.getById(user.getId()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/users/{id}/membership")
    public ResponseEntity<Object> updateMembership(@ApiParam(name = "Id of the user") @PathVariable("id") Long id,
                                                         @RequestParam("isMember") Boolean isMember) {
        User user = userDao.getById(id);
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (isMember) {
            user.addRole(Role.MEMBER);
            user.setMemberSince(Timestamp.from(Instant.now()));
        } else {
            user.removeRole(Role.MEMBER);
            user.setMemberSince(Timestamp.valueOf(LocalDateTime.of(3000, 1, 1, 0, 0)));
        }

        userDao.update(user);
        return new ResponseEntity<>(userDao.getById(user.getId()), HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        User user = userDao.getById(id);
        if (user == null) {
            return new ResponseEntity<>(StatusCodes.NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (!isAuthedForUser(user)) {
            return new ResponseEntity<>(StatusCodes.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        user.setDeletedAt(Timestamp.from(Instant.now()));
        userDao.update(user);
        return new ResponseEntity<>(StatusCodes.OK, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/users/{id}/roles")
    public ResponseEntity<Object> toggleRole(@ApiParam(name = "Id of the user") @PathVariable("id") Long id,
                                                 @RequestParam("role") Role role) {
        User user = userDao.getById(id);
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (user.hasRole(role)) {
            user.removeRole(role);
        } else {
            user.addRole(role);
        }
        userDao.update(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
