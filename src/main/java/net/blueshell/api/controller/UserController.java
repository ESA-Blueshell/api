package net.blueshell.api.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import net.blueshell.db.AdvancedController;
import net.blueshell.enums.Role;
import net.blueshell.api.controller.request.ActivationRequest;
import net.blueshell.api.controller.request.PasswordResetRequest;
import net.blueshell.api.dto.AdvancedUserDTO;
import net.blueshell.api.mapping.RequestMapper;
import net.blueshell.api.mapping.user.AdvancedUserMapper;
import net.blueshell.api.mapping.user.SimpleUserMapper;
import net.blueshell.api.model.User;
import net.blueshell.api.service.UserService;
import net.blueshell.api.validation.group.Creation;
import net.blueshell.api.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends AdvancedController<UserService, AdvancedUserMapper, SimpleUserMapper> {

    private final RequestMapper requestMapper;

    @Autowired
    public UserController(UserService service, AdvancedUserMapper advancedUserMapper, SimpleUserMapper simpleUserMapper, RequestMapper requestMapper) {
        super(service, advancedUserMapper, simpleUserMapper);
        this.requestMapper = requestMapper;
    }

    @PostMapping
    public AdvancedUserDTO create(@Validated(Creation.class) @RequestBody AdvancedUserDTO dto) throws ApiException {
        User user = advancedMapper.fromDTO(dto);
        service.createUser(user);
        return advancedMapper.toDTO(user);
    }

    @PutMapping(value = "/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'write')")
    public AdvancedUserDTO update(@PathVariable("userId") Long userId,
                                  @Validated(Update.class) @RequestBody AdvancedUserDTO dto) throws ApiException {
        dto.setId(userId);
        User user = advancedMapper.fromDTO(dto);
        service.updateUser(user);
        return advancedMapper.toDTO(user);
    }

    @PostMapping(value = "/reset")
    public void resetPassword(@RequestParam("username") String username) throws ApiException {
        service.resetPassword(username);
    }

    @PostMapping(value = "/activate")
    @PreAuthorize("hasPermission(#request, 'User', 'activate')")
    public void activate(@Valid @RequestBody ActivationRequest request) {
        User user = service.findByResetKey(request.getToken());
        requestMapper.fromRequest(request, user);
        service.update(user);
    }

    @PostMapping(value = "/password")
    @PreAuthorize("hasPermission(#request, 'User', 'password')")
    public void setPassword(@Valid @RequestBody PasswordResetRequest request) {
        User user = service.findByResetKey(request.getToken());
        requestMapper.fromRequest(request, user);
        service.update(user);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BOARD')")
    public List<AdvancedUserDTO> getAll(@PathParam("isMember") boolean isMember) {
        List<User> users;
        if (isMember) {
            users = service.findByMembershipNotNull();
        } else {
            users = service.findAll();
        }
        return advancedMapper.toDTOs(users);
    }

    @GetMapping(value = "/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'read')")
    public AdvancedUserDTO getById(@PathVariable("userId") Long userId) {
        User user = service.findById(userId);
        return advancedMapper.toDTO(user);
    }

    @PutMapping(value = "/{id}/membership")
    @PreAuthorize("hasAuthority('BOARD')")
    public AdvancedUserDTO updateMembership(@PathVariable("id") Long userId,
                                            @RequestParam(defaultValue = "isMember") Boolean isMember) {
        User user = service.updateMembership(userId, isMember);
        return advancedMapper.toDTO(user);
    }

    @DeleteMapping(value = "/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'delete')")
    public void delete(@PathVariable("userId") Long userId) {
        service.delete(userId);
    }

    @PutMapping(value = "/{userId}/roles")
    @PreAuthorize("hasPermission(#userId, 'User', 'changeRole')")
    public AdvancedUserDTO toggleRole(@PathVariable("userId") Long userId,
                                      @RequestParam(value = "role", required = true) Role role) {
        User user = service.toggleRole(userId, role);
        return advancedMapper.toDTO(user);
    }

    @PutMapping(value = "/{userId}/membership/toggle")
    @PreAuthorize("hasPermission(#userId, 'User', 'changeRole')")
    public AdvancedUserDTO toggleMembership(@PathVariable("userId") Long userId,
                                      @RequestParam(value = "role", required = true) Role role) {
        User user = service.toggleRole(userId, role);
        return advancedMapper.toDTO(user);
    }

    @GetMapping(value = "/brevo")
    @PreAuthorize("hasPermission(#email, 'User', 'getBrevo')")
    public AdvancedUserDTO getFromBrevo(@RequestParam String email) throws NoSuchFieldException, ApiException, IllegalAccessException {
        User user = service.getFromBrevo(email);
        return advancedMapper.toDTO(user);
    }
}
