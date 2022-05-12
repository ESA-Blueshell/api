package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.committee.CommitteeDao;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.picture.PictureDao;
import net.blueshell.api.business.user.User;
import net.blueshell.api.storage.StorageService;

import java.time.LocalDateTime;

public class EventDTO {

    private static final CommitteeDao committeeDao = new CommitteeDao();
    private static final PictureDao pictureDao = new PictureDao();

    @JsonProperty("committeeId")
    private String committeeId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("location")
    private String location;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("endTime")
    private String endTime;

    @JsonProperty("memberPrice")
    private String memberPrice;

    @JsonProperty("publicPrice")
    private String publicPrice;

    @JsonProperty("visible")
    private boolean visible;

    @JsonProperty("membersOnly")
    private boolean membersOnly;

    @JsonProperty("signUp")
    private boolean signUp;

    @JsonProperty("base64Image")
    private String base64Image;

    @JsonProperty("fileExtension")
    private String fileExtension;

    @JsonProperty("signUpForm")
    private String signUpForm;


    public Event toEvent(StorageService storageService, User uploader) {
        Committee committee = committeeDao.getById(Long.parseLong(committeeId));

        LocalDateTime startTime;
        if (this.startTime != null && !this.startTime.isEmpty()) {
            startTime = LocalDateTime.parse(this.startTime.replace(' ', 'T'));
        } else {
            startTime = null;
        }
        LocalDateTime endTime;
        if (this.endTime != null && !this.endTime.isEmpty()) {
            endTime = LocalDateTime.parse(this.endTime.replace(' ', 'T'));
        } else {
            endTime = null;
        }

        Picture promo;
        if (base64Image == null || fileExtension == null) {
            promo = null;
        } else {
            String filename = storageService.store(base64Image, fileExtension);
            String downloadURL = StorageService.getDownloadURI(filename);

            promo = new Picture(filename, downloadURL, uploader);
            pictureDao.create(promo);
        }

        return new Event(committee, title, description,
                location, startTime, endTime, promo, memberPrice, publicPrice, visible, membersOnly, signUp, signUpForm);
    }

}
