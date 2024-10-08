package net.blueshell.api.business.picture;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.event.Event;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.business.event.EventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PictureController extends AuthorizationController {

    @Autowired
    private PictureDao pictureDao;
    @Autowired
    private EventDao eventDao;

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/pictures")
    public List<Picture> getPictures() {
        return pictureDao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/pictures")
    public Object createPicture(Picture picture) {
        if (picture.getUrl() == null || "".equals(picture.getUrl())) {
            return StatusCodes.BAD_REQUEST;
        }
        pictureDao.create(picture);
        return picture;
    }

    @PreAuthorize("hasAuthority('BOARD')")
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
    public Object getPictureById(
            @ApiParam(name = "Id of the picture")
            @PathVariable("id") String id) {
        Picture pic = pictureDao.getById(Long.parseLong(id));
        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        Event event = eventDao.getById(pic.getEventId());
        if (event != null) {
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
