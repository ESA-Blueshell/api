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
import net.blueshell.api.daos.EventDao;
import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Visibility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

public class CalendarQuickstart {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream("credentials.json");
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

    public static void main(String... args) throws IOException, GeneralSecurityException {
        DatabaseManager.init();
        EventDao dao = new EventDao();


        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();


        Events events = service.events().list("blueshellesports@gmail.com")
                .setTimeMin(new DateTime(1514764800000L))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        events.getItems().forEach(gevent -> {

            Event event = new Event();

            event.setTitle(gevent.getSummary());
            event.setLocation(gevent.getLocation());
            event.setDescription(gevent.getDescription());
            event.setVisibility(Visibility.PUBLIC);
            long startTime;
            if (gevent.getStart().getDateTime() != null) {
                startTime = gevent.getStart().getDateTime().getValue();
            } else {
                startTime = gevent.getStart().getDate().getValue();
            }
            event.setStartTime(new Timestamp(startTime));
            if (!gevent.isEndTimeUnspecified()) {
                if (gevent.getEnd().getDateTime() != null) {
                    event.setEndTime(new Timestamp(gevent.getEnd().getDateTime().getValue()));
                } else {
                    event.setEndTime(new Timestamp(gevent.getEnd().getDate().getValue()));
                }
            } else {
                event.setEndTime(new Timestamp(startTime + 86400000));
            }
            event.setGoogleId(gevent.getHtmlLink().replace("https://www.google.com/calendar/event?eid=", "").split("&tmsrc")[0]);
            dao.create(event);
        });
    }
}