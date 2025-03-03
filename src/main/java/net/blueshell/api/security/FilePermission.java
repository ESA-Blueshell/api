//package net.blueshell.api.security;
//
//import net.blueshell.api.common.enums.Role;
//import net.blueshell.api.model.*;
//import net.blueshell.api.repository.FileRepository;
//import net.blueshell.api.repository.GuestRepository;
//import net.blueshell.api.service.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//
//@Component
//public class FilePermission extends BasePermissionEvaluator<File, Long, FileService> {
//
//    final UserService userService;
//    final EventService eventService;
//    final SponsorService sponsorService;
//
//    @Autowired
//    public FilePermission(FileService service, UserService userService, EventService eventService, SponsorService sponsorService) {
//        super(service);
//        this.userService = userService;
//        this.eventService = eventService;
//        this.sponsorService = sponsorService;
//    }
//
//    @Override
//    boolean canSee(File file, Authentication authentication) {
//        if (hasAuthority(Role.BOARD)) {
//            return true;
//        }
//
//        return switch (file.getType()) {
//            case SIGNATURE -> userService.findBySignature(file) == getPrincipal();
//            case EVENT_BANNER -> {
//                Event event = eventService.findByBanner(file);
//                yield event.isVisible() || event.getCommittee().hasMember(getPrincipal());
//            }
//            case EVENT_PICTURE -> hasAuthority(Role.MEMBER);
//            case PROFILE_PICTURE, DOCUMENT, SPONSOR_PICTURE -> true;
//        };
//    }
//
//    @Override
//    boolean canEdit(File file, Authentication authentication) {
//        if (hasAuthority(Role.BOARD)) {
//            return true;
//        }
//
//        return switch (file.getType()) {
//            case EVENT_PICTURE -> {
//                Event event = eventService.findByBanner(file);
//                yield event.getCommittee().hasMember(getPrincipal());
//            }
//            default -> false; // the rest can only be edited by board
//        };
//    }
//
//    @Override
//    boolean canDelete(File file, Authentication authentication) {
//        if (hasAuthority(Role.BOARD)) {
//            return true;
//        }
//
//        return switch (file.getType()) {
//            case EVENT_PICTURE -> {
//                Event event = eventService.findByBanner(file);
//                yield event.getCommittee().hasMember(getPrincipal());
//            }
//            case PROFILE_PICTURE -> {
//                User user = userService.findByProfilePicture(file);
//                yield user == getPrincipal();
//            } // the rest can only be deleted by board
//            default -> false;
//        };
//    }
//
//
//    public boolean hasPermission(Authentication authentication, Serializable targetName, Object permission) {
//        if (authentication == null || targetName == null || permission == null) {
//            return false;
//        }
//
//        File file = service.findByName(targetName.toString());
//        return hasPermission(authentication, file, permission);
//    }
//}
