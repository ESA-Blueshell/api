package net.blueshell.api.business.eventsignups;

import net.blueshell.api.business.event.Event;
import net.blueshell.api.business.event.EventDao;
import net.blueshell.api.business.guest.Guest;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.service.BrevoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
public class EventSignUpController extends AuthorizationController {
    @Autowired
    private EventDao eventDao;
    @Autowired
    private EventSignUpDao signUpDao;
    @Autowired
    private Dao<Guest> guestDao;

    @Autowired
    private BrevoEmailService emailService;


    @GetMapping(value = "/events/signups")
    public Object getMySignUps() {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.UNAUTHORIZED;
        }
        return signUpDao.list().stream()
                .filter(signUp -> signUp
                        .getUserId() != null && signUp
                        .getUserId() == authedUser.getId())
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/events/signups/{id}")
    public Object getSignUp(@PathVariable("id") String eventId) {
        User authedUser = getPrincipal();
        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null || !event.canSee(authedUser)) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp()) {
            return StatusCodes.FORBIDDEN;
        }
        EventSignUp signUp = signUpDao.getByUserAndEvent(authedUser, event);
        if (signUp == null) {
            return StatusCodes.NOT_FOUND;
        }
        return signUp;
    }

    //TODO: rename all the endpoints in this class, it is getting confusing
    @GetMapping(value = "/events/signups/byAccessToken/{accessToken}")
    public Object getSignUpByHashedId(@PathVariable("accessToken") String accessToken) {
        EventSignUp signUp = signUpDao.getByGuestAccessToken(accessToken);
        if (signUp == null) {
            return StatusCodes.NOT_FOUND;
        }
        return signUp;
    }

    @GetMapping(value = "/events/signups/all/{id}")
    public Object getAllSignUps(@PathVariable("id") String eventId) {
        User authedUser = getPrincipal();
        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null || !event.canSee(authedUser)) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp() || !event.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        return signUpDao.list().stream()
                .filter(signUp -> signUp.getEvent().getId() == event.getId())
                .map(EventSignUpDTO::fromSignUp)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/events/signups/{id}")
    public Object createSignUp(@PathVariable("id") String eventId, @RequestBody(required = false) String formAnswers) {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in.");
        }

        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Could not find an open event with id %s", eventId));
        }

        if (!event.canSee(authedUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find eventId %s cannot be viewed by userId %s", eventId, authedUser.getId()));
        }

        if (!event.isSignUp()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    String.format("Event %s is not available for signup.", eventId));
        }

        if (!authedUser.hasRole(Role.MEMBER) && event.isMembersOnly()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    String.format("UserId %s cannot join eventId %s as it is member-only.", authedUser.getId(), eventId));
        }

        // Check if the formAnswers are formatted correctly
        if (event.getSignUpForm() != null && !event.validateAnswers(formAnswers)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The signup form answers are invalid.");
        }

        // Check if we're updating an existing sign-up or not
        EventSignUp oldSignUp = signUpDao.getByUserAndEvent(authedUser, event);
        if (oldSignUp != null) {
            oldSignUp.setFormAnswers(formAnswers);

            signUpDao.update(oldSignUp);
            return oldSignUp;
        }

        // Create new sign-up
        LocalDateTime signedUpAt = LocalDateTime.now();
        EventSignUp signUp = new EventSignUp(event, authedUser, null, formAnswers, signedUpAt);
        signUpDao.create(signUp);
        return signUp;
    }

    @PutMapping(value = "/events/{eventId}/signups/{id}")
    public Object updateSignUp(@PathVariable("eventId") String eventId, @PathVariable("id") String signUpId, @RequestBody String formAnswers) {
        // For now, this method only applies to guest sign-ups
        EventSignUp signUp = signUpDao.getById(Long.parseLong(signUpId));

        if (signUp == null || signUp.getGuest() == null || signUp.getEvent().getId() != Long.parseLong(eventId)) {
            return StatusCodes.NOT_FOUND;
        }

        signUp.setFormAnswers(formAnswers);
        signUpDao.update(signUp);
        return signUp;
    }

    @PostMapping(value = "/events/signups/{id}/guest")
    public Object createGuestSignUp(
            @PathVariable("id") String eventId,
            @RequestBody EventGuestSignUpDTO signUpDTO) {

        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null || !event.isVisible()) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp() || event.isMembersOnly()) {
            return StatusCodes.FORBIDDEN;
        }

        Guest createdGuest = guestDao.create(signUpDTO.toGuest());
        if (createdGuest == null) {
            return StatusCodes.INTERNAL_SERVER_ERROR;
        }


        // Create new sign-up
        LocalDateTime signedUpAt = LocalDateTime.now();
        EventSignUp signUp = new EventSignUp(event, null, createdGuest, signUpDTO.getAnswers(), signedUpAt);
        signUpDao.create(signUp);

        // Send email to guest
        emailService.sendEventSignupEmail(signUp);

        return signUp;
    }

    @DeleteMapping(value = "/events/signups/{id}")
    public Object removeSignUp(@PathVariable("id") String eventId) {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.UNAUTHORIZED;
        }
        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        EventSignUp eventSignUp = signUpDao.getByUserAndEvent(authedUser, event);
        if (eventSignUp == null) {
            return StatusCodes.NOT_FOUND;
        }

        signUpDao.delete(eventSignUp.getId());
        return StatusCodes.OK;
    }


    @DeleteMapping(value = "/events/{eventId}/signups/{id}")
    public Object removeSignUp(@PathVariable("eventId") String eventId, @PathVariable("id") String signUpId) {
        // For now, this method only applies to guest sign-ups
        EventSignUp signUp = signUpDao.getById(Long.parseLong(signUpId));
        if (signUp == null || signUp.getGuest() == null || signUp.getEvent().getId() != Long.parseLong(eventId)) {
            return StatusCodes.NOT_FOUND;
        }

        signUpDao.delete(signUp.getId());
        return StatusCodes.OK;
    }
}
