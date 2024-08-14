package net.blueshell.api.business.sponsor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.picture.Picture;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sponsors")
@Data
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "logo_id")
    @JsonIgnore
    private Picture picture;

    public Sponsor() {
    }

    @JsonProperty("picture")
    public long getPictureId() {
        return getPicture() == null ? 0 : getPicture().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sponsor sponsor = (Sponsor) o;
        return id == sponsor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
