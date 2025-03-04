package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import net.blueshell.api.base.BaseModel;
import net.blueshell.api.model.converter.FormAnswerListConverter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "event_signups")
@Data
@SQLDelete(sql = "UPDATE event_signups SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class EventSignUp implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "event_id", insertable=false, updatable=false)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable=false, updatable=false)
    private Long userId;

    @OneToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Convert(converter = FormAnswerListConverter.class)
    @Column(name = "form_answers", columnDefinition = "TEXT")
    private List<Object> formAnswers;

    @Column(name = "signed_up_at")
    private LocalDateTime signedUpAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @JsonProperty("event")
    public Long getEventId() {
        return getEvent() == null ? null : getEvent().getId();
    }
}
