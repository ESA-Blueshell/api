package net.blueshell.api.business.event;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.storage.StorageService;
import net.blueshell.api.util.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class EventController extends AuthorizationController {

    private final EventDao dao;
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final GoogleCalendarService calendarService;

    public EventController(EventDao dao, EventRepository eventRepository, StorageService storageService, GoogleCalendarService calendarService) {
        this.dao = dao;
        this.eventRepository = eventRepository;
        this.storageService = storageService;
        this.calendarService = calendarService;
    }


    @PreAuthorize("hasAuthority('COMMITTEE')")
    @PostMapping(value = "/events")
    public Object createEvent(@RequestBody EventDTO eventDTO) {
        User authedUser = getPrincipal();
        Event event = eventDTO.toEvent(storageService, authedUser);
        // Check if user is part of the event's committee or is board
        if (!event.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        // Check if the user is allowed to edit public events (only board should be able to do this)
        if (event.isVisible() && !hasAuthorization(Role.BOARD)) {
            return StatusCodes.FORBIDDEN;
        }
        // If the event has a sign-up, check if the new event's form is properly formatted
        if (event.getSignUpForm() != null && !event.hasValidSignUpForm()) {
            return StatusCodes.BAD_REQUEST;
        }
        if (event.isVisible()) {
            // If event is visible, add to google calendar
            try {
                String googleId = calendarService.addToGoogleCalendar(event);
                event.setGoogleId(googleId);
            } catch (IOException e) {
                e.printStackTrace();
                return StatusCodes.INTERNAL_SERVER_ERROR;
            }
        }
        // Add to database
        event.setCreator(authedUser);
        event.setLastEditor(authedUser);
        dao.create(event);
        return event;
    }

    @GetMapping(value = "/events/{id}")
    public Object getEventById(
            @ApiParam(name = "Id of the event")
            @PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        return event;
    }

    @GetMapping(value = "/events")
    public List<Event> getEvents(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Predicate<Event> predicate = event ->
                event != null
                && event.isVisible()
                        && (from == null
                            || (to == null
                                ? event.inMonth(from)
                                : event.inRange(from, to)
                                )
                );

        return dao.list().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Event::getStartTime))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/events/upcoming")
    public List<Event> getUpcomingEvents(@RequestParam(required = false) boolean editable) {
        Predicate<Event> predicate;

        User authedUser = getPrincipal();
        if (editable) {
            predicate = event -> (event.getStartTime() == null || event.getStartTime().isAfter(LocalDateTime.now())) && event.canEdit(authedUser);
        } else {
            predicate = event -> (event.getStartTime() == null || event.getStartTime().isAfter(LocalDateTime.now())) && event.isVisible();
        }

        return dao.list().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Event::getStartTime))
                .collect(Collectors.toList());
    }




    @GetMapping(value = "/events/past")
    public List<Event> getPastEvents(@RequestParam(required = false) boolean editable) {
        Predicate<Event> predicate;

        User authedUser = getPrincipal();
        if (editable) {
            predicate = event -> event.getStartTime().isBefore(LocalDateTime.now()) && event.canEdit(authedUser);
        } else {
            predicate = event -> event.getStartTime().isBefore(LocalDateTime.now()) && event.isVisible();
        }

        return dao.list().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Event::getStartTime).reversed())
                .limit(30)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/events/eventPageable")
    Page<Event> eventPageable(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @DeleteMapping(value = "/events/{id}")
    public Object deleteEventById(@PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        // Check if user is part of the event's committee or is board
        User authedUser = getPrincipal();
        if (!event.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        // Delete the event in the google calendar
        if (event.getGoogleId() != null) {
            try {
                calendarService.removeFromGoogleCalendar(event.getGoogleId());
            } catch (IOException e) {
                e.printStackTrace();
                return StatusCodes.INTERNAL_SERVER_ERROR;
            }
        }
        // Delete the event in the database
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

    @PutMapping(value = "/events/{id}")
    public Object createOrUpdateEvent(@PathVariable("id") String id, @RequestBody EventDTO eventDTO) {
        User authedUser = getPrincipal();
        Event oldEvent = dao.getById(Long.parseLong(id));
        // Check if event exists
        if (oldEvent == null || !oldEvent.canSee(authedUser)) {
            return StatusCodes.NOT_FOUND;
        }
        Event newEvent = eventDTO.toEvent(storageService, authedUser);
        // Check if user is allowed to edit the old and new event
        if (!(oldEvent.canEdit(authedUser) && newEvent.canEdit(authedUser))) {
            return StatusCodes.FORBIDDEN;
        }
        // Check if the user is allowed to edit public events (only board should be able to do this)
        if (!hasAuthorization(Role.BOARD) && newEvent.isVisible() && !oldEvent.isVisible()) {
            return StatusCodes.FORBIDDEN;
        }
        // If the event has a sign-up, check if the new event's form is properly formatted
        if (newEvent.getSignUpForm() != null && !newEvent.hasValidSignUpForm()) {
            return StatusCodes.BAD_REQUEST;
        }

        // Handle event visibility
        try {
            if (newEvent.isVisible()) {
                String googleId;
                if (oldEvent.isVisible()) {
                    // Update event in googel calendar
                    googleId = oldEvent.getGoogleId();
                    calendarService.updateGoogleCalendar(googleId, newEvent);
                } else {
                    // Add to google calendar
                    googleId = calendarService.addToGoogleCalendar(newEvent);
                }
                newEvent.setGoogleId(googleId);

            } else if (oldEvent.getGoogleId() != null) {
                // Old event was on google calendar and new event is not visible, so remove it from google calendar
                calendarService.removeFromGoogleCalendar(oldEvent.getGoogleId());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return StatusCodes.INTERNAL_SERVER_ERROR;
        }

        // Update event in database
        newEvent.setId(Long.parseLong(id));
        newEvent.setCreator(oldEvent.getCreator());
        newEvent.setLastEditor(authedUser);
        if (newEvent.getBanner() == null) {
            newEvent.setBanner(oldEvent.getBanner());
        }
        dao.update(newEvent);
        return StatusCodes.OK;
    }

}
