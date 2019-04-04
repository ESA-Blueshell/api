package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

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
    private User uploader;

    @JsonIgnore
    @Column(name = "uploader_id", updatable=false, insertable=false)
    private Long uploaderFk;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Picture() {
        createdAt = Timestamp.from(Instant.now());
    }

    public Picture(String name, String url, User uploader) {
        this();
        this.name = name;
        this.url = url;
        this.uploader = uploader;
        this.uploaderFk = uploader.getId();
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

    public Long getUploader() {
        return uploaderFk;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public void setUploaderFk(Long uploader_fk) {
        this.uploaderFk = uploader_fk;
    }
}
