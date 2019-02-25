package net.blueshell.api.model;

import javax.persistence.*;

@Entity
@Table(name = "sponsors")
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "logo_id")
    private Picture picture;
}
