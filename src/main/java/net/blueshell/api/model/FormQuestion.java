package net.blueshell.api.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.blueshell.api.base.BaseModel;

import java.util.List;

@Data
public class FormQuestion implements BaseModel {
    @NotBlank
    private String prompt;
    @NotBlank
    private String type;
    private List<@NotBlank String> options;
}
