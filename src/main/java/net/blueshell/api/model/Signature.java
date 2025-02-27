package net.blueshell.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import net.blueshell.api.base.BaseModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Getter
@Entity
@SQLDelete(sql = "UPDATE signatures SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "signatures")
@Data
public class Signature implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String url;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "date")
    private Date date;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Signature() {
        createdAt = Timestamp.from(Instant.now());
    }

    public Signature(String name, String url, Date date, String city) {
        this();
        this.name = name;
        this.url = url;
        this.date = date;
        this.city = city;
    }
}
