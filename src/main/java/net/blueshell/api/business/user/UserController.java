package net.blueshell.api.business.user;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.contribution.Contribution;
import net.blueshell.api.business.contribution.ContributionDao;
import net.blueshell.api.business.contribution.ContributionPeriod;
import net.blueshell.api.business.contribution.ContributionPeriodDao;
import net.blueshell.api.business.signature.Signature;
import net.blueshell.api.business.signature.SignatureDao;
import net.blueshell.api.business.user.request.EnableAccountRequest;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private BrevoEmailService brevoEmailService;

    @Autowired
    private UserDao dao;

    @Autowired
    private BrevoContactService brevoContactService;


    @Autowired
    private ContributionDao contributionDao;

    @Autowired
    private ContributionPeriodDao contributionPeriodDao;


    private final StorageService storageService;

    public UserController(StorageService storageService) {
        this.storageService = storageService;
    }

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

    @PostMapping(value = "/createAccount")
    public Object createUser(@RequestBody AdvancedUserDTO userDto) {
        if (!userDto.getUsername().matches("[a-zA-Z0-9]+")) {
            return new ResponseEntity<>("Invalid username, must only contain alphanumeric characters.", HttpStatus.BAD_REQUEST);
        }

        User userWithSameName = dao.getByUsername(userDto.getUsername());
        if (userWithSameName != null) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }

        User userWithSameEmail = dao.getByEmail(userDto.getEmail());
        if (userWithSameEmail != null) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        }

        User userWithSamePhone = dao.getByPhoneNumber(userDto.getPhoneNumber());
        if (userWithSamePhone != null) {
            return new ResponseEntity<>("Phone number is already taken.", HttpStatus.BAD_REQUEST);
        }

        var user = userDto.mapToBasicUser();
        fillInInitialFields(user);
        dao.create(user);

        List<Contribution> contributions = new ArrayList<>();
        for (ContributionPeriod contributionPeriod : contributionPeriodDao.list()) {
            contributions.add(new Contribution(user, contributionPeriod));
        }
        contributionDao.createAll(contributions);

        sendAccountCreationEmail(user);
        updateMembership(userDto, user);

        return StatusCodes.OK;
    }

    @PutMapping(value = "/users/{id}")
    public Object updateUser(@ApiParam(name = "Id of the user") @PathVariable("id") String id, @RequestBody AdvancedUserDTO userDto) {
        var user = dao.getById(Long.parseLong(id));
        if (user == null || !isAuthedForUser(user)) {
            return new ResponseEntity<>("User not found.", HttpStatus.BAD_REQUEST);
        }

        User userWithSamePhone = dao.getByPhoneNumber(userDto.getPhoneNumber());
        if (userWithSamePhone != null && user.getId() != userWithSamePhone.getId()) {
            return new ResponseEntity<>("Phone number is already taken.", HttpStatus.BAD_REQUEST);
        }

        UserModule.applyUserDtoToUser(userDto, user);
        updateMembership(userDto, user);
        return StatusCodes.OK;
    }

    private void updateMembership(AdvancedUserDTO userDto, User user) {
        // If the user became a member using the online signup form, make them a member
        if (user.getSignature() == null && userDto.getSignature() != null) {
            UserModule.makeMember(userDto, user, storageService);
            brevoEmailService.sendInitialMembershipEmail(user);
        }

        dao.update(user);

        user = dao.getById(user.getId());
        // If the user is a member, update his entry in the membership database (brevo).
        if (user.hasRole(Role.MEMBER)) {
            brevoContactService.createOrUpdateContact(user);
        }
    }


    private void fillInInitialFields(User user) {
        user.setCreatedAt(TimeUtil.of(LocalDateTime.now()));
        if (user.getMemberSince() == null) {
            // When using createMember endpoint a correct value will already be set
            user.setMemberSince(TimeUtil.of(LocalDateTime.of(3000, 1, 1, 0, 0)));

            // Enabled by default when using createUser.
            user.setNewsletter(true);
        }
    }

    public void sendAccountCreationEmail(User user) {
        user.setResetType(ResetType.INITIAL_ACCOUNT_CREATION);
        user.setResetKey(Util.getRandomCapitalString(INITIAL_ACCOUNT_KEY_LENGTH));
        user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(INITIAL_ACCOUNT_KEY_VALID_SECONDS)));
        brevoEmailService.sendAccountCreationEmail(user);
    }

    @GetMapping(value = "/users/{id}")
    public Object getUserById(@ApiParam(name = "Id of the user") @PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(user)) {
            return StatusCodes.FORBIDDEN;
        }
        return user;
    }

    @Autowired
    private SignatureDao signatureDao;


    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!isAuthedForUser(user)) {
            return StatusCodes.FORBIDDEN;
        }

        Signature signature = user.getSignature();
        if (signature != null) {
            user.setSignature(null);
            dao.update(user);

            signatureDao.delete(signature.getId());
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

    @PostMapping(value = "/enableAccount")
    public ResponseEntity<Object> enableUserByEmaillink(@RequestBody EnableAccountRequest request) {
        if (request == null || !request.isValid()) {
            return new ResponseEntity<>("Missing username/password.", HttpStatus.BAD_REQUEST);
        }

        User user = dao.getByUsername(request.getUsername());
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            return new ResponseEntity<>("Reset key has expired.", HttpStatus.BAD_REQUEST);
        }

        if (!request.getToken().equals(user.getResetKey()) || user.getResetType() != ResetType.INITIAL_ACCOUNT_CREATION) {
            return new ResponseEntity<>("Invalid key.", HttpStatus.BAD_REQUEST);
        }

        user.setResetKey(null);
        user.setResetType(null);
        user.setResetKeyValidUntil(null);
        user.setEnabled(true);

        dao.update(user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/users/{id}/member")
    public ResponseEntity<Object> makeUserMember(@ApiParam(name = "Id of the user") @PathVariable("id") String id, @ApiParam(name = "To enable/disable membership") @QueryParam("isMember") Boolean isMember) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (isMember) {
            user.addRole(Role.MEMBER);
            user.setMemberSince(Timestamp.from(Instant.now()));
        } else {
            user.removeRole(Role.MEMBER);
            user.setMemberSince(TimeUtil.of(LocalDateTime.of(3000, 1, 1, 0, 0)));
        }

        dao.update(user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/users/{id}/contributionPaid")
    public ResponseEntity<Object> setContributionPaid(@ApiParam(name = "Id of the user") @PathVariable("id") String id, @ApiParam(name = "To enable/disable membership") @QueryParam("member") Boolean contributionPaid) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (contributionPaid == null) {
            contributionPaid = false;
        }

        user.setContributionPaid(contributionPaid);
        dao.update(user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/users/{id}/addToBrevo")
    public ResponseEntity<Object> addToBrevo(@ApiParam(name = "Id of the user") @PathVariable("id") String id) {
        User user = dao.getById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        brevoContactService.createOrUpdateContact(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/users/password")
    public ResponseEntity<Object> sendResetMail(@QueryParam("username") String username) {
        User user = dao.getByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        }

        if (user.getResetType() == ResetType.INITIAL_ACCOUNT_CREATION) {
            user.setResetKey(Util.getRandomCapitalString(INITIAL_ACCOUNT_KEY_LENGTH));
            user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(INITIAL_ACCOUNT_KEY_VALID_SECONDS)));
            brevoEmailService.sendAccountCreationEmail(user);
            brevoEmailService.sendInitialMembershipEmail(user);
        } else {
            user.setResetKey(Util.getRandomCapitalString(PASSWORD_RESET_KEY_LENGTH));
            user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(PASSWORD_RESET_KEY_VALID_SECONDS)));
            user.setResetType(ResetType.PASSWORD_RESET);
            brevoEmailService.sendPasswordResetEmail(user);
        }

        dao.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/users/password")
    public ResponseEntity<Object> resetUserPassword(@RequestBody PasswordResetRequest resetRequest) {
        if (resetRequest == null || !resetRequest.isValid()) {
            return new ResponseEntity<>("Not every field in the reset request was filled", HttpStatus.BAD_REQUEST);
        }

        User user = dao.getByUsername(resetRequest.getUsername());
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
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
        return ResponseEntity.ok().build();
    }
}
