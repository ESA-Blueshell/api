package net.blueshell.api;

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
import com.google.api.services.calendar.model.Events;
import net.blueshell.api.business.event.Event;
import net.blueshell.api.db.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarQuickstart {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Yeets away everything in the events table and fills it up with updated events
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        DatabaseManager.init();

        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();

//            long startTime = 1514764800000L; //01-01-2019
//            long startTime = 1514764800000L; //01-01-2018

            long currentTime = System.currentTimeMillis();
            long startTime = currentTime - 2629800000L; //one month ago

            // Remove all events that could be updated (now it's events 1 month ago)
            session.createSQLQuery("delete from events where UNIX_TIMESTAMP(events.start_time) > " + startTime / 1000).executeUpdate();

            // Setting up connection to the google calendar
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            long lastTime = currentTime + 15778800000L; //6 months ahead
//            long lastTime = currentTime + 31557600000L; //12 months ahead

            while (startTime < lastTime) {
                long endTime = startTime + 2629800000L; // add a month
                addEvents(session, service, startTime, endTime);
                startTime = endTime;
            }

            // Coommit all changes
            t.commit();
        }

        DatabaseManager.getSessionFactory().close();
        System.exit(100);
    }

    private static void addEvents(Session session, Calendar service, long startTime, long endTime) throws IOException {
        // TODO: https://discord.com/channels/324285132133629963/390108059994685440/816774856196227102
        // Getting all events from the blueshell calendar since 01-01-2019*
        Events events = service.events().list("87r5v7ep7k9ronlrg8n2q9033s@group.calendar.google.com")
                .setTimeMin(new DateTime(startTime))
                .setTimeMax(new DateTime(endTime))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        events.getItems().forEach(gevent -> {

            Event event = new Event();

            event.setTitle(gevent.getSummary());
            event.setLocation(gevent.getLocation());
            event.setDescription(gevent.getDescription());
            event.setVisible(true);
            event.setMembersOnly(false);
            event.setSignUp(false);
            // Check if it's an all day event or not
            if (gevent.getStart().getDateTime() == null) {
                event.setStartTime(LocalDate.parse(gevent.getEnd().getDate().toString(), DateTimeFormatter.ISO_DATE).atStartOfDay());
                event.setEndTime(LocalDate.parse(gevent.getEnd().getDate().toString(), DateTimeFormatter.ISO_DATE)
                        // vuetify's calendar doesn't like it when there's an event until midnight
                        .atStartOfDay().minusMinutes(1));
            } else {
                // Set start time
                event.setStartTime(LocalDateTime.parse(gevent.getStart().getDateTime().toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));

                // vuetify's calendar doesn't like it when there's an event until midnight
                event.setEndTime(LocalDateTime.parse(gevent.getEnd().getDateTime().toString().replace("00:00:00", "23:59:00"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            event.setGoogleId(gevent.getId());
            session.save(event);
        });
    }
}
