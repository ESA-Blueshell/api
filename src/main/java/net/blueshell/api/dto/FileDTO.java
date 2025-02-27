package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.blueshell.api.base.DTO;
import net.blueshell.api.dto.user.SimpleUserDTO;
import net.blueshell.api.enums.FileType;
import net.blueshell.api.validation.group.Administration;
import net.blueshell.api.validation.group.Creation;
import org.springframework.http.MediaType;

import java.sql.Timestamp;

@Data
public class FileDTO extends DTO {

    private Long id;

    private String name;

    private String url;

    private SimpleUserDTO uploader;

    private Timestamp createdAt;

    /**
     * Mime type of File
     */
    private MediaType mediaType;

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

