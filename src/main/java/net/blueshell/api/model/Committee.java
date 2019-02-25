package net.blueshell.api.model;

import javax.persistence.*;

@Entity
@Table(name = "committees")
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;
}
