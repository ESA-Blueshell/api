package net.blueshell.api.model.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.blueshell.api.model.FormAnswer;

import java.util.List;

@Converter
public class FormAnswerListConverter implements AttributeConverter<List<FormAnswer>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<FormAnswer> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion error when writing FormAnswer list", e);
        }
    }

    @Override
    public List<FormAnswer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<FormAnswer>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion error when reading FormAnswer list", e);
        }
    }
}
