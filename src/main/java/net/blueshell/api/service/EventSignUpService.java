package net.blueshell.api.service;

import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.exception.ResourceNotFoundException;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.User;
import net.blueshell.api.repository.EventSignUpRepository;
import net.blueshell.api.service.brevo.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sendinblue.ApiException;

import java.util.List;

@Service
public class EventSignUpService extends BaseModelService<EventSignUp, Long, EventSignUpRepository> {

    private final GuestService guestService;
    private final EventService eventService;
    private final EmailService emailService;

    @Autowired
    public EventSignUpService(EventSignUpRepository repository,
                              GuestService guestService,
                              EventService eventService,
                              EmailService emailService) {
        super(repository);
        this.guestService = guestService;
        this.eventService = eventService;
        this.emailService = emailService;
    }

    @Override
    protected Long extractId(EventSignUp eventSignUp) {
        return eventSignUp.getId();
    }


    @Transactional(readOnly = true)
    public EventSignUp findByUserAndEventId(User user, Long eventId) {
        return repository.findByUserAndEventId(user, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("EventSignUp not found for user: "
                        + user.getId() + " and event: " + eventId));
    }

    @Transactional(readOnly = true)
    public EventSignUp findByGuestAccessToken(String accessToken) {
        return repository.findByGuestAccessToken(accessToken)
                .orElseThrow(() -> new ResourceNotFoundException("EventSignUp not found with accessToken: " + accessToken));
    }

    @Transactional
    public EventSignUp createSignUp(Long eventId, EventSignUp signUp) throws ApiException {
        Event event = eventService.findById(eventId);
        signUp.setEvent(event);
        if (signUp.getGuest() != null) {
            emailService.sendEventSignupEmail(signUp);
        }
        return create(signUp);
    }

    @Transactional(readOnly = true)
    public void deleteSignUp(Long eventId, String accessToken) {
        EventSignUp signUp;
        if (accessToken == null) {
            signUp = findByUserAndEventId(getPrincipal(), eventId);
        } else {
            signUp = findByGuestAccessToken(accessToken);
        }
        deleteById(signUp.getId());
    }

    public List<EventSignUp> findByUser(User user) {
        return repository.findByUser(user);
    }

    public List<EventSignUp> findByEventId(Long eventId) {
        return repository.findByEventId(eventId);
    }
}
