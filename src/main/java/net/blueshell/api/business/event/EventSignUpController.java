package net.blueshell.api.business.event;

import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
public class EventSignUpController extends AuthorizationController {
    private final Dao<Event> eventDao = new EventDao();
    private final EventSignUpDao signUpDao = new EventSignUpDao();


    @GetMapping(value = "/events/signups")
    public Object getMySignUps() {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.UNAUTHORIZED;
        }
        return signUpDao.list().stream().filter(signUp -> signUp.getUserId() == authedUser.getId()).collect(Collectors.toList());
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
        EventSignUpId id = new EventSignUpId(authedUser, event);
        EventSignUp signUp = signUpDao.getById(id);
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
            return StatusCodes.UNAUTHORIZED;
        }

        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null || !event.canSee(authedUser)) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp()) {
            return StatusCodes.FORBIDDEN;
        }


        // Check if the formAnswers are formatted correctly
        if (event.getSignUpForm() != null && !event.validateAnswers(formAnswers)) {
            return StatusCodes.BAD_REQUEST;
        }

        // Check if we're updating an existing sign-up or not
        EventSignUp oldSignUp = signUpDao.getById(new EventSignUpId(authedUser, event));
        if (oldSignUp != null) {
            LocalDateTime signedUpAt = oldSignUp.getSignedUpAt();
            EventSignUp signUp = new EventSignUp(authedUser, event, formAnswers, signedUpAt);
            signUpDao.update(signUp);
            return StatusCodes.OK;
        }

        // Create new sign-up
        LocalDateTime signedUpAt = LocalDateTime.now();
        EventSignUp signUp = new EventSignUp(authedUser, event, formAnswers, signedUpAt);
        signUpDao.create(signUp);
        return StatusCodes.CREATED;
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
        EventSignUpId id = new EventSignUpId(authedUser, event);
        EventSignUp eventSignUp = signUpDao.getById(id);
        if (eventSignUp == null) {
            return StatusCodes.NOT_FOUND;
        }

        signUpDao.delete(id);
        return StatusCodes.OK;
    }
}
