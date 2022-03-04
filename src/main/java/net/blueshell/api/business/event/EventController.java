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
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class EventController extends AuthorizationController {

    private final Dao<Event> dao = new EventDao();
    private final UserDao userDao = new UserDao();
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
        User authedUser = getPrincipal();
        if (authedUser == null) {
            return StatusCodes.FORBIDDEN;
        }
        Set<Long> authedUserCommitteeIds = authedUser.getCommitteeIds();
        Event event = eventDTO.toEvent();
        try {
            if (authedUserCommitteeIds.contains(event.getCommitteeId()) || hasAuthorization(Role.BOARD)) {
                try {
                    String googleId = addToGoogleCalendar(event);
                    event.setGoogleId(googleId);
                } catch (IOException e) {
                    e.printStackTrace();
                    return StatusCodes.INTERNAL_SERVER_ERROR;
                }
                dao.create(event);
            } else {
                System.out.println(authedUserCommitteeIds);
                System.out.println(event.getCommitteeId());
                return StatusCodes.FORBIDDEN;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return event;
    }

    private String addToGoogleCalendar(Event event) throws IOException {
        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();
        googleEvent.setSummary(event.getTitle())
                .setDescription(event.getDescription())
                .setLocation(event.getLocation());

        //TODO: check if timezones are correct!!
        DateTime startDateTime = new DateTime(event.getStartTime().getTime());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndTime().getTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setEnd(end);
        //TODO: CHANGE TO BLUESHELL CALENDAR!!!!!!!!!1!!!!!111!!
        //TODO: This is now live
        String calendarId = "c_kqp2ru792pn7ghnra32802b3mg@group.calendar.google.com";
        googleEvent = service.events().insert(calendarId, googleEvent).execute();
        return googleEvent.getHtmlLink().replace("https://www.google.com/calendar/event?eid=", "").split("&tmsrc")[0];
    }

    @GetMapping(value = "/events/{id}")
    public Object getEventById(
            @ApiParam(name = "Id of the event")
            @PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.canSee(getPrincipal())) {
            return StatusCodes.NOT_FOUND;
        }
        return event;
    }

    @GetMapping(value = "/events")
    public List<Event> getEvents(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Predicate<Event> timePredicate = event -> from == null || (to == null ? event.inMonth(from) : event.inRange(from, to));

        return dao.list().stream()
                .filter(timePredicate)
//                .filter(event -> event.canSee(userDao.getByUsername(getAuthorizedUsername())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //TODO: TEST THIS SHIT
    @PreAuthorize(("hasAuthority('BOARD')"))
    @DeleteMapping(value = "/events/{id}")
    public Object deleteEventById(@PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if(event == null) {
            return StatusCodes.NOT_FOUND;
        }
        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();
        googleEvent.remove(event.getGoogleId(), event);
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

    //TODO: TEST THIS SHIT
    @PreAuthorize(("hasAuthority('BOARD')"))
    @PutMapping(value = "/events/{id}")
    public Object createOrUpdateEvent(@RequestBody EventDTO eventDTO) {
        Event evnt = dao.getById(eventDTO.toEvent().getId());
        if(evnt == null) {
            // create new event
            return createEvent(eventDTO);
        } else {
            dao.update(evnt);

        }
        return StatusCodes.OK;
    }
}
