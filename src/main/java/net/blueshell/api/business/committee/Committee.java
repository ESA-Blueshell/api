package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
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

    public Committee() {
    }

    @JsonProperty("members")
    public Set<Long> getMemberIds() {
        Set<Long> set = new HashSet<>();
        if (getMembers() == null) {
            return set;
        }
        for (CommitteeMembership cm : getMembers()) {
            set.add(cm.getUserId());
        }
        return set;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Committee committee = (Committee) o;
        return id == committee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean hasMember(String username) {
        return getMembers().stream()
                .map(CommitteeMembership::getUser)
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    public boolean hasMember(User user) {
        return getMembers().stream()
                .anyMatch(cm -> cm.getUser().equals(user));
    }
}
