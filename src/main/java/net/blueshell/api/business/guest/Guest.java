package net.blueshell.api.business.guest;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCrypt;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "guests")
@Data
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;
    @Column
    private String discord;
    @Column
    private String email;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "access_token")
    private String accessToken;


    public Guest() {
    }

    public Guest(String name, String discord, String email) {
        this.name = name;
        this.discord = discord;
        this.email = email;
        this.createdAt = Timestamp.from(Instant.now());
        this.accessToken = BCrypt.hashpw(name + discord + email + this.createdAt, BCrypt.gensalt())
                .replace("/", "");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest user = (Guest) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
