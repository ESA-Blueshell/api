package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.EventDao;
import net.blueshell.api.daos.PictureDao;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Picture;
import net.blueshell.api.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PictureController extends AuthorizationController {

    private final Dao<Picture> pictureDao = new PictureDao();
    private final Dao<Event> eventDao = new EventDao();
    private final UserDao userDao = new UserDao();

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/pictures")
    public List<Picture> getPictures() {
        return pictureDao.list();
    }

    @PostMapping(value = "/pictures")
    public Object createPicture(Picture picture) {
        if (picture.getUrl() == null || "".equals(picture.getUrl())) {
            return StatusCodes.BAD_REQUEST;
        }
        pictureDao.create(picture);
        return picture;
    }

    @PutMapping(value = "/pictures/{id}")
    public Object createOrUpdatePicture(Picture picture) {
        Picture pic = pictureDao.getById(picture.getId());
        if (pic == null) {
            // create new picture
            return createPicture(picture);
        } else {
            pictureDao.update(pic);
        }
        return StatusCodes.OK;
    }

    @GetMapping(value = "/pictures/{id}")
    public Object getPictureById(@PathVariable("id") String id) {
        Picture pic = pictureDao.getById(Long.parseLong(id));

        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        Event event = eventDao.getById(pic.getEventId());
        if (event != null && event.canSee(userDao.getByUsername(getAuthorizedUsername()))) {
            return pic;
        } else {
            return StatusCodes.FORBIDDEN;
        }
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/pictures/{id}")
    public Object deletePictureById(@PathVariable("id") String id) {
        Picture pic = pictureDao.getById(Long.parseLong(id));
        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        pictureDao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

}
