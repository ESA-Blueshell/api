package net.blueshell.api.controller;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Picture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController(value = "/api/pictures")
public class PictureController {

    @RequestMapping("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Picture getPictureById(@PathVariable("id") String id) {
        return (Picture) DatabaseManager.getObjFromDB(Picture.class, Integer.parseInt(id));
    }

    @RequestMapping
    public String getDefault() {
        return DatabaseManager.getObjFromDB(Picture.class, 1).toString();
    }
}
