package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import net.blueshell.api.base.BaseModel;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.converter.FormQuestionListConverter;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "event_pictures")
@Data
@SQLDelete(sql = "UPDATE event_pictures SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class EventPicture implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "picture_id")
    private File picture;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}
