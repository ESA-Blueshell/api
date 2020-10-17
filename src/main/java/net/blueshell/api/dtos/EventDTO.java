package net.blueshell.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.daos.CommitteeDao;
import net.blueshell.api.daos.PictureDao;
import net.blueshell.api.model.Committee;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Picture;
import net.blueshell.api.model.Visibility;

import java.sql.Timestamp;

public class EventDTO {

    CommitteeDao committeeDao = new CommitteeDao();
    PictureDao pictureDao = new PictureDao();

    @JsonProperty("committeeId")
    private String committeeId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("visibility")
    private String visibility;

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


    public Event toEvent() {
        Committee committee = committeeDao.getById(Integer.parseInt(committeeId));
        Visibility visibility = Visibility.valueOf(this.visibility);
        Timestamp startTime = Timestamp.valueOf(this.startTime);
        Timestamp endTime = Timestamp.valueOf(this.endTime);
        Picture banner = pictureDao.getById(Integer.parseInt(bannerId));
        return new Event(committee, title, description, visibility,
                location, startTime, endTime, banner, memberPrice, publicPrice);
    }

}
