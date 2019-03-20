package net.blueshell.api.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "committee_members")
public class CommitteeMembership implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "committee_id")
    private Committee committee;

    private String role;
}
