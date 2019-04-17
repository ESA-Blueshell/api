package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "registrations")
@Data
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_state")
    private RegistrationState registrationState;

    @OneToOne
    @JoinColumn(name = "accepted_by_user_id")
    @JsonIgnore
    private User acceptBy;

    @Column(name = "accepted_at")
    private Timestamp createdAt;

    @JsonProperty("user")
    public long getUserId() {
        return getUser() == null ? 0 : getUser().getId();
    }

    @JsonProperty("acceptedBy")
    public long getAcceptedById() {
        return getAcceptBy() == null ? 0 : getAcceptBy().getId();
    }
}
