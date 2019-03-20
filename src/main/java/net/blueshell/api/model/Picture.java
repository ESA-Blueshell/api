package net.blueshell.api.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;


@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String url;

    @OneToOne
    @JoinColumn(name = "uploader_id")
    private User user;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Picture() {
        createdAt = Timestamp.from(Instant.now());
    }

    public Picture(String name, String url, User user) {
        this();
        this.name = name;
        this.url = url;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
