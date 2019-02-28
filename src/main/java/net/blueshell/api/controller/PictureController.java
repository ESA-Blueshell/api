package net.blueshell.api.controller;

import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.PictureDao;
import net.blueshell.api.model.Picture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PictureController {

    private final Dao<Picture> dao = new PictureDao();

    @GetMapping(value = "/pictures")
    public List<Picture> getPictures() {
        return dao.list();
    }

    @PostMapping(value = "/pictures")
    public Object createPicture(Picture picture) {
        if (picture.getUrl() == null || "".equals(picture.getUrl())) {
            return StatusCodes.BAD_REQUEST;
        }
        dao.create(picture);
        return StatusCodes.CREATED;
    }

    // TODO put

    @GetMapping(value = "/pictures/{id}")
    public Object getPictureById(@PathVariable("id") String id) {
        Picture pic = dao.getById(Long.parseLong(id));
        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        return pic;
    }

    @DeleteMapping(value = "/pictures/{id}")
    public Object deletePictureById(@PathVariable("id") String id) {
        Picture pic = dao.getById(Long.parseLong(id));
        if (pic == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }

}
