package net.blueshell.api.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import net.blueshell.api.business.event.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleCalendarService {

    @Value("${googleCalendar.id}")
    private String calendarId;
    @Value("${googleCalendar.clientId}")
    private String clientId;
    @Value("${googleCalendar.clientEmail}")
    private String clientEmail;
    @Value("${googleCalendar.privateKeyPkcs8}")
    private String privateKeyPkcs8;
    @Value("${googleCalendar.privateKeyId}")
    private String privateKeyId;

    private static final String APPLICATION_NAME = "Blueshell Google Calendar API";

    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS);

    private final HtmlRenderer htmlRenderer;
    private final Parser htmlParser;

    private final Calendar service;

    public GoogleCalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credentials = ServiceAccountCredentials
                .fromPkcs8(clientId, clientEmail, privateKeyPkcs8, privateKeyId, SCOPES);

        // Build a new authorized API client service.
        service = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();


        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create()
        ));

        htmlParser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();
        // uncomment to convert soft-breaks to hard breaks
//        options.set(HtmlRenderer.SOFT_BREAK, "");
//        options.set(HtmlRenderer.HARD_BREAK, "");
    }


    public String addToGoogleCalendar(Event event) throws IOException {
        var googleEvent = toGoogleEvent(event);
        googleEvent = service.events()
                .insert(calendarId, googleEvent)
                .execute();
        return googleEvent.getId();
    }

    public void updateGoogleCalendar(String googleId, Event newEvent) throws IOException {
        var googleEvent = toGoogleEvent(newEvent);
        service.events()
                .update(calendarId, googleId, googleEvent)
                .execute();
    }

    public void removeFromGoogleCalendar(String googleId) throws IOException {
        service.events()
                .delete(calendarId, googleId)
                .execute();
    }


    private com.google.api.services.calendar.model.Event toGoogleEvent(Event event) {
        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();
        googleEvent.setSummary(event.getTitle())
                .setLocation(event.getLocation());

        //Convert description from markdown to html for cool styling
        String preProcessedHtml = htmlRenderer.render(htmlParser.parse(event.getDescription()));
        preProcessedHtml = preProcessedHtml.replaceAll("<p>", "");
        preProcessedHtml = preProcessedHtml.replaceAll("</p>", "");
        googleEvent.setDescription(preProcessedHtml);

        DateTime startDateTime = new DateTime(event.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setEnd(end);
        return googleEvent;
    }
}
