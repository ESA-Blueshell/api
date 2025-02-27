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

    @AssertTrue(message = "Options are required for radio and checkbox types")
    public boolean isOptionsValid() {
        if ("radio".equals(type) || "checkbox".equals(type)) {
            return options != null && !options.isEmpty();
        }
        return true;
    }
}
