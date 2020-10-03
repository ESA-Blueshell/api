package net.blueshell.api.controller;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.EventDao;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.dtos.EventDTO;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Role;
import net.blueshell.api.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class EventController extends AuthorizationController {

    private final Dao<Event> dao = new EventDao();
    private final UserDao userDao = new UserDao();

    @PostMapping(value = "/events")
    public Object createEvent(EventDTO eventDTO) {
        User authedUser = super.getAuthedUser();
        if (authedUser == null) {
            return StatusCodes.FORBIDDEN;
        }
        Set<Long> authedUserCommitteeIds = authedUser.getCommitteeIds();
        Event event = eventDTO.toEvent();
        try {
            if (authedUserCommitteeIds.contains(event.getCommitteeId()) || hasAuthorization(Role.BOARD)) {
                dao.create(event);
            } else {
                System.out.println(authedUserCommitteeIds);
                System.out.println(event.getCommitteeId());
                return StatusCodes.FORBIDDEN;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
        return event;
    }

    @GetMapping(value = "/events/{id}")
    public Object getEventById(
            @ApiParam(name = "Id of the event")
            @PathVariable("id") String id) {
        Event event = dao.getById(Long.parseLong(id));
        if (event == null) {
            return StatusCodes.NOT_FOUND;
        }
        if (!event.canSee(userDao.getByUsername(getAuthorizedUsername()))) {
            return StatusCodes.FORBIDDEN;
        }
        return event;
    }

    @GetMapping(value = "/events")
    public List<Event> getEvents(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Predicate<Event> timePredicate = event -> from == null || (to == null ? event.inMonth(from) : event.inRange(from, to));

        return dao.list().stream()
                .filter(timePredicate)
//                .filter(event -> event.canSee(userDao.getByUsername(getAuthorizedUsername())))
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
