package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.committee.CommitteeDao;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.picture.PictureDao;

import java.sql.Timestamp;

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

    @JsonProperty("bannerId")
    private String bannerId;

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

    @JsonProperty("signUpForm")
    private String signUpForm;


    public Event toEvent() {
        Committee committee = committeeDao.getById(Integer.parseInt(committeeId));
        Timestamp startTime = Timestamp.valueOf(this.startTime);
        Timestamp endTime = Timestamp.valueOf(this.endTime);
        //TODO: Promo picture currently disabled
//        Picture banner = pictureDao.getById(Integer.parseInt(bannerId));
        Picture banner = null;
        return new Event(committee, title, description,
                location, startTime, endTime, banner, memberPrice, publicPrice, visible, membersOnly, signUp, signUpForm);
    }

}
