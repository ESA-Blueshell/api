package net.blueshell.api.business.signature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.business.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Getter
@Entity
@Table(name = "signatures")
@Data
public class Signature {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    private String name;

    @Setter
    private String url;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "date")
    @Setter
    private Date date;

    @Column(name = "city")
    @Setter
    private String city;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Setter
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
