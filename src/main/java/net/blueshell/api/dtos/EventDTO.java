package net.blueshell.api.dtos;

import lombok.Data;
import net.blueshell.api.daos.CommitteeDao;
import net.blueshell.api.daos.PictureDao;
import net.blueshell.api.model.Committee;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Picture;
import net.blueshell.api.model.Visibility;
import java.sql.Timestamp;
import java.time.Instant;

@Data
public class EventDTO {

    CommitteeDao committeeDao = new CommitteeDao();
    PictureDao pictureDao = new PictureDao();

    private String committeeId;

    private String title;

    private String description;

    private String visibility;

    private String location;

    private String startTime;

    private String bannerId;

    private String memberPrice;

    private String publicPrice;

    public Event toEvent() {
        Committee committee = committeeDao.getById(Integer.parseInt(committeeId));
        Visibility visibility = Visibility.valueOf(this.visibility);
        Timestamp startTime = Timestamp.valueOf(this.startTime);
        Picture banner = pictureDao.getById(Integer.parseInt(bannerId));
        return new Event(committee, title, description, visibility,
        location, startTime, banner, memberPrice, publicPrice);
    }

}
