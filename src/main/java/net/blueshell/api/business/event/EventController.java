package net.blueshell.api.business.event;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.storage.StorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class EventController extends AuthorizationController {


    //TODO: CHANGE TO BLUESHELL CALENDAR!!!!!!!!!1!!!!!111!!
    //TODO: This is now the sitecie calendar lol
    private static final String GOOGLE_CALENDAR_ID = "9cugc57n2rc43p6o1r0povepe0@group.calendar.google.com";

    private final Dao<Event> dao = new EventDao();
    private final StorageService storageService;
    private Calendar service;

    {
        final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
        final String TOKENS_DIRECTORY_PATH = "tokens";
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            InputStream in = new FileInputStream("credentials.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credentials = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Build a new authorized API client service.
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            System.exit(65656565);
        }
    }

    public EventController(StorageService storageService) {
        this.storageService = storageService;
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
                String googleId = addToGoogleCalendar(event);
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
        User authedUser = getPrincipal();

        Predicate<Event> predicate = event -> event.canSee(authedUser) &&
                (from == null || (to == null ? event.inMonth(from) : event.inRange(from, to)));

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
            predicate = event -> event.getStartTime().isAfter(LocalDateTime.now()) && event.canEdit(authedUser);
        } else {
            predicate = event -> event.getStartTime().isAfter(LocalDateTime.now()) && event.canSee(authedUser);
        }

        return dao.list().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Event::getStartTime))
                .collect(Collectors.toList());
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
        try {
            removeFromGoogleCalendar(event.getGoogleId());
        } catch (IOException e) {
            e.printStackTrace();
            return StatusCodes.INTERNAL_SERVER_ERROR;
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
                    updateGoogleCalendar(googleId, newEvent);
                } else {
                    // Add to google calendar
                    googleId = addToGoogleCalendar(newEvent);
                }
                newEvent.setGoogleId(googleId);

            } else if (oldEvent.getGoogleId() != null) {
                // Old event was on google calendar and new event is not visible, so remove it from google calendar
                removeFromGoogleCalendar(oldEvent.getGoogleId());
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

    private void updateGoogleCalendar(String googleId, Event newEvent) throws IOException {
        com.google.api.services.calendar.model.Event googleEvent = newEvent.toGoogleEvent();
        service.events().update(GOOGLE_CALENDAR_ID, googleId, googleEvent).execute();
    }


    private String addToGoogleCalendar(Event event) throws IOException {
        com.google.api.services.calendar.model.Event googleEvent = event.toGoogleEvent();
        googleEvent = service.events().insert(GOOGLE_CALENDAR_ID, googleEvent).execute();
        return googleEvent.getId();
    }


    private void removeFromGoogleCalendar(String googleId) throws IOException {
        service.events().delete(GOOGLE_CALENDAR_ID, googleId).execute();
    }


}
