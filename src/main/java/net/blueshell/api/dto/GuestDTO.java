package net.blueshell.api.dto;

import lombok.Data;
import net.blueshell.api.base.DTO;

import java.sql.Timestamp;
import java.util.Objects;

@Data
public class GuestDTO extends DTO {

    private long id;

    private String name;

    private String discord;

    private String email;

    private Timestamp createdAt;

    private String accessToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestDTO user = (GuestDTO) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
