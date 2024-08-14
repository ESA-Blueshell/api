package net.blueshell.api.business.picture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.event.Event;
import net.blueshell.api.business.user.User;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "pictures")
@Data
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String url;

    @OneToOne
    @JoinColumn(name = "uploader_id")
    @JsonIgnore
    private User uploader;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    public Picture() {
        createdAt = Timestamp.from(Instant.now());
    }

    public Picture(String name, String url, User uploader) {
        this();
        this.name = name;
        this.url = url;
        this.uploader = uploader;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    @JsonProperty("uploader")
    public long getUploaderId() {
        return getUploader().getId();
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @JsonProperty("event")
    public long getEventId() {
        return event.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return id == picture.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
