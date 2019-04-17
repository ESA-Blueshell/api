package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

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

    @JsonProperty("picture")
    public long getPictureId() {
        return getPicture() == null ? 0 : getPicture().getId();
    }
}
