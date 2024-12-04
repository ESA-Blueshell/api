package net.blueshell.api.business.user;

import net.blueshell.api.business.user.request.ActivateMemberRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sendinblue.ApiException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MemberController extends AuthorizationController {

    private static final int MEMBER_ACTIVATION_KEY_LENGTH = 25;
    private static final long MEMBER_ACTIVATION_VALID_SECONDS = 3600L * 24 * 365 * 100; // 100 years (crazy long right?)

    @Autowired
    private BrevoEmailService brevoEmailService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BrevoContactService brevoContactService;

    private final StorageService storageService;

    public MemberController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/members")
    public ResponseEntity<Object> create(@RequestBody AdvancedUserDTO userDto) {
        if (userDao.getByEmail(userDto.getEmail()) != null) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        } else if (userDao.getByPhoneNumber(userDto.getPhoneNumber()) != null) {
            return new ResponseEntity<>("Phone number is already taken.", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.addRole(Role.MEMBER);
        user.setConsentPrivacy(true); // If a member of the association they have consented to our privacy policy
        UserModule.applyEditableFields(user, userDto);
        if (getPrincipal() == null) {
            UserModule.applyCreationFields(user, userDto);
        } else if (getPrincipal().hasRole(Role.SECRETARY) || getPrincipal().hasRole(Role.ADMIN)) {
            UserModule.applyAdminFields(user, userDto);
        }

        try {
            brevoContactService.createOrUpdateContact(user);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(e.getResponseBody());
        }

        brevoEmailService.sendContributionEmail(user);
        sendMemberActivationEmail(user);
        userDao.create(user);

        // May only be done after creation due to foreign key relation
        UserModule.storeSignature(user, userDto, storageService);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    private void sendMemberActivationEmail(User user) {
        user.setResetKey(Util.getRandomCapitalString(MEMBER_ACTIVATION_KEY_LENGTH));
        user.setResetKeyValidUntil(Timestamp.from(Instant.now().plusSeconds(MEMBER_ACTIVATION_VALID_SECONDS)));
        user.setResetType(ResetType.MEMBER_ACTIVATION);
        brevoEmailService.sendMemberActivationEmail(user);
    }


    @PostMapping(value = "/members/activate")
    public ResponseEntity<Object> activate(@RequestBody ActivateMemberRequest request) {
        if (request == null || !request.isValid()) {
            return new ResponseEntity<>("Missing required fields.", HttpStatus.BAD_REQUEST);
        }

        User user = userDao.getByResetKey(request.getToken());
        if (user == null) {
            return new ResponseEntity<>("Could not find that account.", HttpStatus.NOT_FOUND);
        } else if (user.getResetType() != ResetType.MEMBER_ACTIVATION) {
            return new ResponseEntity<>("Invalid reset type.", HttpStatus.BAD_REQUEST);
        } else if (TimeUtil.hasExpired(user.getResetKeyValidUntil())) {
            return new ResponseEntity<>("Activation key has expired.", HttpStatus.BAD_REQUEST);
        } else if (userDao.getByUsername(request.getUsername()) != null) {
            return new ResponseEntity<>("Username is already taken.", HttpStatus.BAD_REQUEST);
        }

        UserModule.applyCreationFields(user, request);
        UserModule.activateAccount(user);
        userDao.update(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/members")
    @PreAuthorize("hasAuthority('BOARD')")
    public List<SimpleUserDTO> getAll() {
        return userDao.list().stream()
                .filter(user -> user.hasRole(Role.MEMBER))
                .map(SimpleUserDTO::fromUser)
                .collect(Collectors.toList());
    }
}
