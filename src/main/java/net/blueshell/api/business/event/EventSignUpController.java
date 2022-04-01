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
    private final Dao<EventSignUp> signUpDao = new EventSignUpDao();


    @GetMapping(value = "/events/signup")
    public Object getMySignUps() {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.UNAUTHORIZED;
        }
        return signUpDao.list().stream().filter(signUp -> signUp.getUserId() == authedUser.getId()).collect(Collectors.toList());
    }

    @GetMapping(value = "/events/signup/{id}")
    public Object getSignUp(@PathVariable("id") String eventId) {
        User authedUser = getPrincipal();
        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp() || !event.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        EventSignUpId id = new EventSignUpId(authedUser, event);
        EventSignUp signUp = signUpDao.getById(id);
        if (signUp == null) {
            return StatusCodes.NOT_FOUND;
        }
        return signUp;
    }

    @GetMapping(value = "/events/signup/all/{id}")
    public Object getAllSignUps(@PathVariable("id") String eventId) {
        User authedUser = getPrincipal();
        Event event = eventDao.getById(Long.parseLong(eventId));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.isSignUp() || !event.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        return signUpDao.list().stream().filter(signUp -> signUp.getEvent().getId() == event.getId()).collect(Collectors.toList());
    }

    @PostMapping(value = "/events/signup/{id}")
    public Object createSignUp(@PathVariable("id") String eventId, @RequestBody(required = false) String signUpForm) {
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.UNAUTHORIZED;
        }

        Event event = eventDao.getById(Long.parseLong(eventId));
        if (!event.isSignUp() || !event.isVisible()) {
            return StatusCodes.FORBIDDEN;
        }
        if (event.getSignUpForm() != null && (signUpForm == null || signUpForm.equals(""))) {
            return StatusCodes.BAD_REQUEST;
        }
        LocalDateTime signedUpAt = LocalDateTime.now();

        EventSignUp signUp = new EventSignUp(authedUser, event, signUpForm, signedUpAt);
        signUpDao.create(signUp);
        return StatusCodes.CREATED;
    }

    @DeleteMapping(value = "/events/signup/{id}")
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
