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
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class EventController extends AuthorizationController {


    //TODO: CHANGE TO BLUESHELL CALENDAR!!!!!!!!!1!!!!!111!!
    //TODO: This is now the sitecie calendar lol
    private static final String GOOGLE_CALENDAR_ID = "9cugc57n2rc43p6o1r0povepe0@group.calendar.google.com";

    private final Dao<Event> dao = new EventDao();

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

    @PostMapping(value = "/events")
    public Object createEvent(@RequestBody EventDTO eventDTO) {
        Event event = eventDTO.toEvent();
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.FORBIDDEN;
        }
        // Check if user is part of the event's committee or is board
        if (!authedUser.getCommitteeIds().contains(event.getCommitteeId()) && !hasAuthorization(Role.BOARD)) {
            return StatusCodes.FORBIDDEN;
        }
        // Add to google calendar
        try {
            com.google.api.services.calendar.model.Event googleEvent = event.toGoogleEvent();
            googleEvent = service.events().insert(GOOGLE_CALENDAR_ID, googleEvent).execute();
            event.setGoogleId(googleEvent.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return StatusCodes.INTERNAL_SERVER_ERROR;
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

        Predicate<Event> predicate = event -> event.canSee(authedUser)
                && from == null || (to == null ? event.inMonth(from) : event.inRange(from, to));

        return dao.list().stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @GetMapping(value = "/events/upcoming")
    public List<Event> getUpcomingEvents(@RequestParam(required = false) boolean editable) {
        Predicate<Event> predicate;

        if (editable) {
            User authedUser = getPrincipal();
            predicate = event -> event.getStartTime().isAfter(LocalDateTime.now()) && event.canEdit(authedUser);
        } else {
            predicate = event -> event.getStartTime().isAfter(LocalDateTime.now());
        }

        return dao.list().stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
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
            service.events().delete(GOOGLE_CALENDAR_ID, event.getGoogleId()).execute();
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
        Event oldEvent = dao.getById(Long.parseLong(id));
        Event newEvent = eventDTO.toEvent();
        // Check if event exists
        if (oldEvent == null) {
            return StatusCodes.NOT_FOUND;
        }
        // Check if user is allowed to edit the old and new event
        User authedUser = getPrincipal();
        if (!(oldEvent.canEdit(authedUser) || newEvent.canEdit(authedUser))) {
            return StatusCodes.FORBIDDEN;
        }

        // Update event in googel calendar
        String googleId = oldEvent.getGoogleId();
        newEvent.setGoogleId(googleId);
        try {
            com.google.api.services.calendar.model.Event googleEvent = newEvent.toGoogleEvent();
            service.events().update(GOOGLE_CALENDAR_ID, googleId, googleEvent).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return StatusCodes.INTERNAL_SERVER_ERROR;
        }

        // Update event in database
        newEvent.setId(Long.parseLong(id));
        newEvent.setLastEditor(authedUser);
        dao.update(newEvent);
        return StatusCodes.OK;
    }
}
