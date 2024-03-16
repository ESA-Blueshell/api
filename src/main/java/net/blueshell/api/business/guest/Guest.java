package net.blueshell.api.business.guest;

import lombok.Data;

import javax.persistence.*;
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
    @Column
    private Timestamp createdAt;

    public Guest() {
    }

    public Guest(String name, String discord, String email) {
        this.name = name;
        this.discord = discord;
        this.email = email;
        this.createdAt = Timestamp.from(Instant.now());
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
