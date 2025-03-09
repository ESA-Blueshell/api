package net.blueshell.api.security;

import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.File;
import net.blueshell.api.model.User;
import net.blueshell.api.security.base.BasePermissionEvaluator;
import net.blueshell.api.service.EventService;
import net.blueshell.api.service.FileService;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class FilePermission extends BasePermissionEvaluator<File, Long, FileService> {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public FilePermission(FileService service, UserService userService, EventService eventService) {
        super(service);
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, String permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }
        File file = (File) targetDomainObject;
        User principal = (User) authentication.getPrincipal();

        if (principal.hasRole(Role.BOARD)) {
            return true;
        }

        return switch (permission) {
            case "read" -> handleReadPermission(file, principal);
            case "write" -> handleWritePermission(file, principal);
            case "delete" -> handleDeletePermission(file, principal);
            default -> false;
        };
    }

    private boolean handleReadPermission(File file, User principal) {
        switch (file.getType()) {
            case SIGNATURE:
                User user = userService.findBySignature(file);
                return user != null && user.equals(principal);
            case EVENT_BANNER:
                Event event = eventService.findByBanner(file);
                return event != null && (event.isVisible() || event.getCommittee().hasMember(principal));
            case EVENT_PICTURE:
                return principal.hasRole(Role.MEMBER);
            case PROFILE_PICTURE:
            case DOCUMENT:
            case SPONSOR_PICTURE:
                return true;
            default:
                return false;
        }
    }

    private boolean handleWritePermission(File file, User principal) {
        if (file.getType() == FileType.EVENT_PICTURE) {
            Event event = eventService.findByBanner(file);
            return event != null && event.getCommittee().hasMember(principal);
        }
        return false;
    }

    private boolean handleDeletePermission(File file, User principal) {
        switch (file.getType()) {
            case EVENT_PICTURE:
                Event event = eventService.findByBanner(file);
                return event != null && event.getCommittee().hasMember(principal);
            case PROFILE_PICTURE:
                User user = userService.findByProfilePicture(file);
                return user != null && user.equals(principal);
            default:
                return false;
        }
    }

    @Override
    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }
        File file = service.findById((Long) targetId);
        return file != null && hasPermission(authentication, file, permission);
    }
}