package net.blueshell.api.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "committees")
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "committee")
    private Set<CommitteeMembership> members;

    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> subscribers;
}
