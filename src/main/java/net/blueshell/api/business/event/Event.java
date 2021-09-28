package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.billable.Billable;
import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private String location;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

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

    public Event() {
    }

    public Event(Committee committee, String title, String description, Visibility visibility, String location, Timestamp startTime, Timestamp endTime, Picture banner, String memberPrice, String publicPrice) {
        this.committee = committee;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.banner = banner;
        this.memberPrice = Double.parseDouble(memberPrice);
        this.publicPrice = Double.parseDouble(publicPrice);
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

    public boolean canSee(User user) {
        Visibility vis = getVisibility();
        // public: available to everyone
        // internal: members only
        // private: committee only
        if (user == null) {
            return vis == Visibility.PUBLIC;
        }
        boolean canSee = vis == Visibility.PUBLIC || user.hasRole(Role.MEMBER);
        if (canSee && vis == Visibility.PRIVATE) {
            canSee = user.hasRole(Role.BOARD) || getCommittee().hasMember(user);
        }
        return canSee;
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
        try {
            Timestamp fromTimestamp = new Timestamp(new SimpleDateFormat("yyyy-MM").parse(from).getTime());
            Timestamp toTimestamp = new Timestamp(new SimpleDateFormat("yyyy-MM").parse(to).getTime());
            return startTime.after(fromTimestamp) && startTime.before(toTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
