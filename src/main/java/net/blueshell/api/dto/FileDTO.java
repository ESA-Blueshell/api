package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.dto.user.SimpleUserDTO;
import org.springframework.http.MediaType;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileDTO extends DTO {

    private Long id;

    private String name;

    private String url;

    private long uploaderId;

    private Timestamp createdAt;

    /**
     * Mime type of File
     */
    private String mediaType;

    private Long size;

    /**
     * The original filename (e.g. "document.pdf").
     * Does not need to be provided
     */
    private String fileName;

    private FileType type;

    /**
     * The file content encoded in Base64.
     * Only needed during upload
     */
    private String base64Content;
}

