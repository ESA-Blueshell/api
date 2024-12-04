package net.blueshell.api.business.signature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.storage.StorageService;

import java.util.Date;

@Data
public class SignatureDTO {
    @JsonProperty
    private String data;

    @JsonProperty
    private Date date;

    @JsonProperty
    private String city;

    @JsonProperty
    private String country;

    public Signature mapToSignature(StorageService storageService) {
        if (this.getData() != null && this.getDate() != null && this.getCity() != null && this.getCountry() != null) {
            String filename = storageService.storeSignature(this.getData(), ".png");
            String downloadURL = storageService.getDownloadURI(filename);
            return new Signature(filename, downloadURL, this.getDate(), this.getCity());
        } else {
            return null;
        }
    }
}
