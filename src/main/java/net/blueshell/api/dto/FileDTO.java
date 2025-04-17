package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.dto.user.SimpleUserDTO;
import net.blueshell.common.dto.BaseDTO;
import org.springframework.http.MediaType;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileDTO extends BaseDTO {

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

    private FileType fileType;

    /**
     * The file content encoded in Base64.
     * Only needed during upload
     */
    private String base64Content;
}

