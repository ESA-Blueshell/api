package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.Data;
import net.blueshell.api.business.billable.Billable;
import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Entity
@Table(name = "events")
@Data
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User creator;

    @OneToOne
    @JoinColumn(name = "last_editor_id")
    @JsonIgnore
    private User lastEditor;

    @OneToOne
    @JoinColumn(name = "committee_id")
    @JsonIgnore
    private Committee committee;

    @JoinColumn(name = "title")
    private String title;

    @JoinColumn(name = "description")
    private String description;

    @JoinColumn(name = "location")
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(name = "banner_id")
    @JsonIgnore
    private Picture banner;

    @Column(name = "price_member")
    private Double memberPrice;

    @Column(name = "price_public")
    private Double publicPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    @JsonIgnore
    private Set<EventFeedback> feedbacks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    @JsonIgnore
    private Set<Billable> billables;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private Set<Picture> pictures;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "visible")
    private boolean visible;

    @Column(name = "members_only")
    private boolean membersOnly;

    @Column(name = "sign_up")
    private boolean signUp;

    @Column(name = "sign_up_form")
    private String signUpForm;


    public Event() {
    }

    public Event(Committee committee, String title, String description, String location, LocalDateTime startTime, LocalDateTime endTime, Picture banner, String memberPrice, String publicPrice, boolean visible, boolean membersOnly, boolean signUp, String signUpForm) {
        this.committee = committee;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.banner = banner;
        this.memberPrice = Double.parseDouble(memberPrice);
        this.publicPrice = Double.parseDouble(publicPrice);
        this.visible = visible;
        this.membersOnly = membersOnly;
        this.signUp = signUp;
        this.signUpForm = signUpForm;
    }

    /**
     * Get the next month
     *
     * @param month the month is formatted as "yyyy-MM"
     * @return the next month in the format "yyyy-MM"
     */
    private static String nextMonth(String month) {
        final String[] splitMonth = month.split("-");
        if (splitMonth[1].equals("12"))
            return (Integer.parseInt(splitMonth[0]) + 1) + "-01";
        return splitMonth[0] + "-" + (Integer.parseInt(splitMonth[1]) + 1);
    }

    @JsonProperty("creator")
    public long getCreatorId() {
        return getCreator() == null ? 0 : getCreator().getId();
    }

    @JsonProperty("lastEditor")
    public long getLastEditorId() {
        return getLastEditor() == null ? 0 : getLastEditor().getId();
    }

    @JsonProperty("committee")
    public long getCommitteeId() {
        return getCommittee() == null ? 0 : getCommittee().getId();
    }

    @JsonProperty("banner")
    public String getBannerId() {
        return getBanner() == null ? null : getBanner().getUrl();
    }

    @JsonProperty("feedbacks")
    public Set<Long> getFeedbackIds() {
        Set<Long> set = new HashSet<>();
        if (getFeedbacks() == null) {
            return set;
        }
        for (EventFeedback ef : getFeedbacks()) {
            set.add(ef.getId());
        }
        return set;
    }

    @JsonProperty("billables")
    public Set<Long> getBillableIds() {
        Set<Long> set = new HashSet<>();
        if (getBillables() == null) {
            return set;
        }
        for (Billable b : getBillables()) {
            set.add(b.getId());
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Checks if this Event is in the given month
     *
     * @param month the month is formatted as "yyyy-MM"
     * @return true if the Event is in the month
     */
    public boolean inMonth(String month) {
        return inRange(month, nextMonth(month));
    }

    /**
     * Checks if the Event is in the given range of months (exclusive)
     *
     * @param from the month is formatted as "yyyy-MM"
     * @param to   the month is formatted as "yyyy-MM"
     * @return true if the Event is in the range
     */
    public boolean inRange(String from, String to) {
        String[] fromSplit = from.split("-");
        LocalDateTime fromDateTime = LocalDateTime.of(Integer.parseInt(fromSplit[0]), Integer.parseInt(fromSplit[1]), 1, 0, 0);
        String[] toSplit = to.split("-");
        LocalDateTime toDateTime = LocalDateTime.of(Integer.parseInt(toSplit[0]), Integer.parseInt(toSplit[1]), 1, 0, 0);
        return startTime.isAfter(fromDateTime) && startTime.isBefore(toDateTime);
    }

    com.google.api.services.calendar.model.Event toGoogleEvent() {
        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();
        googleEvent.setSummary(title)
                .setDescription(description)
                .setLocation(location);

        DateTime startDateTime = new DateTime(startTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setStart(start);

        DateTime endDateTime = new DateTime(endTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Amsterdam");
        googleEvent.setEnd(end);
        return googleEvent;
    }

    public boolean canSee(User user) {
        return visible || canEdit(user);
    }

    public boolean canEdit(User user) {
        // Check if user has board authority OR if user is in the event's committee
        return user != null && (user.getAuthorities().stream().anyMatch(auth -> Role.valueOf(auth.getAuthority()).matchesRole(Role.BOARD))
                || (committee != null && user.getCommitteeIds().contains(committee.getId())));
    }


    /**
     * Check if this Event's sign up form is formatted properly
     *
     * @return true if all's good
     */
    public boolean validateSignUpForm() {
        if (signUpForm == null) {
            return false;
        }
        try {
            JSONParser eventFormParser = new JSONParser(signUpForm);
            ArrayList<Object> eventSignUpForm = eventFormParser.list();
            eventFormParser.ensureEOF();

            if (eventSignUpForm.size() == 0) return false;

            for (Object questionObj : eventSignUpForm) {
                LinkedHashMap<String, Object> question = (LinkedHashMap<String, Object>) questionObj;

                if (!question.containsKey("prompt") || !question.containsKey("type")) return false;

                String type = (String) question.get("type");
                if (!type.equals("open") && !type.equals("radio") && !type.equals("checkbox"))
                    return false;

                if ("radio".equals(type) || "checkbox".equals(type)) {
                    // Schizo checking
                    if (!question.containsKey("formAnswers") ||
                            !(question.get("formAnswers") instanceof List) ||
                            !(((List) question.get("formAnswers")).stream().allMatch(opt -> opt instanceof String))) {
                        return false;
                    }
                }
            }
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * Check if the given answers string is compatible with this event's sign up form
     *
     * @param answers answers to this event's sign up form, formatted as a json array, with entries of type string, int or array<int> (example: ["hello",3,[0,2]])
     * @return true if all's good
     */
    public boolean validateAnswers(String answers) {
        if (signUpForm == null || answers == null || answers.equals("")) {
            return false;
        }
        try {
            JSONParser eventFormParser = new JSONParser(signUpForm);
            ArrayList<Object> signUpFormList = eventFormParser.list();
            eventFormParser.ensureEOF();

            JSONParser answersParser = new JSONParser(answers);
            ArrayList<Object> answersList = answersParser.list();
            answersParser.ensureEOF();

            if (signUpFormList.size() != answersList.size()) {
                return false;
            }
            for (int i = 0; i < signUpFormList.size(); i++) {
                Object questionObj = signUpFormList.get(i);
                LinkedHashMap<String, Object> question = (LinkedHashMap<String, Object>) questionObj;
                switch ((String) question.get("type")) {
                    case "open":
                        if (!(answersList.get(i) instanceof String)) return false;
                        break;
                    case "radio":
                        if (!(answersList.get(i) instanceof BigInteger)) return false;
                        break;
                    case "checkbox":
                        if (!(answersList.get(i) instanceof List) ||
                                !(((List) answersList.get(i)).stream().allMatch(opt -> opt instanceof BigInteger)))
                            return false;
                        break;
                }
            }
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
