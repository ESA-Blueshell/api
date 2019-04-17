package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "committees")
@Data
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "committee")
    @JsonIgnore
    private Set<CommitteeMembership> members;

    @ManyToMany(mappedBy = "subscriptions")
    @JsonIgnore
    private Set<User> subscribers;

    @JsonProperty("subscribers")
    public Set<Long> getSubscriberIds() {
        Set<Long> set = new HashSet<>();
        if (getSubscribers() == null) {
            return set;
        }
        for (User user : getSubscribers()) {
            set.add(user.getId());
        }
        return set;
    }

}
