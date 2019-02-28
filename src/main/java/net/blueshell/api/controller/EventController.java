package net.blueshell.api.controller;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.EventDao;
import net.blueshell.api.model.Event;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final Dao<Event> dao = new EventDao();

    @GetMapping(value = "/events/{id}")
    public Object getEventById(@PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if (event == null) {
            return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
        }
        return event;
    }

    @GetMapping(value = "/events")
    public List<Event> getEvents() {
        return dao.list();
    }

}
