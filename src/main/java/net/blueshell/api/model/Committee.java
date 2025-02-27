package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import net.blueshell.api.base.BaseModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "committees")
@Data
@SQLDelete(sql = "UPDATE committees SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Committee implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "committee")
    @JsonIgnore
    private Set<CommitteeMember> committeeMembers;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    public Committee() {
    }

    @JsonProperty("members")
    public Set<Long> getMemberIds() {
        Set<Long> set = new HashSet<>();
        if (getCommitteeMembers() != null) {
            for (CommitteeMember cm : getCommitteeMembers()) {
                set.add(cm.getUserId());
            }
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
        return getCommitteeMembers().stream()
                .map(CommitteeMember::getUser)
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    public boolean hasMember(User user) {
        return getCommitteeMembers().stream().anyMatch(cm -> cm.getUser().equals(user));
    }
}
