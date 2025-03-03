package net.blueshell.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.base.BaseModel;
import net.blueshell.api.common.enums.FileType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.MediaType;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "files")
@SQLDelete(sql = "UPDATE files SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
public class File implements BaseModel {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    @OneToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "media_type")
    private MediaType mediaType;

    @Column(name = "size")
    private Long size;

    @Column(name = "type")
    private FileType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id == file.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
