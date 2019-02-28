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

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                '}';
    }
}
