package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.base.DTO;

@Getter
@Setter
public class NewsDTO extends DTO {
    private String id;

    @NotBlank(message = "creatorId is required.")
    private String creatorId;

    private String creatorUsername;

    @NotBlank(message = "lastEditorId is required.")
    private String lastEditorId;

    private String lastEditorUsername;

    @NotBlank(message = "News type is required.")
    private String newsType;

    @NotBlank(message = "News title cannot be blank.")
    @Size(max = 200, message = "Title cannot exceed 200 characters.")
    private String title;

    @NotBlank(message = "Content cannot be blank.")
    private String content;

    private String postedAt;
}
