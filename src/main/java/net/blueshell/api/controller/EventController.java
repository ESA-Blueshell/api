package net.blueshell.api.controller;

import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.Data;
import net.blueshell.api.base.BaseController;
import net.blueshell.api.dto.EventDTO;
import net.blueshell.api.mapping.EventMapper;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.User;
import net.blueshell.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RestController
@RequestMapping("/events")
public class EventController extends BaseController<EventService, EventMapper> {

    @Autowired
    public EventController(EventService service, EventMapper mapper) {
        super(service, mapper);
    }

    @PreAuthorize("hasAuthority('COMMITTEE')")
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) throws IOException {
        Event event = mapper.fromDTO(eventDTO);
        service.createEvent(event);
        return ResponseEntity.ok(mapper.toDTO(event));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Event', 'see')")
    public ResponseEntity<EventDTO> getEventById(
            @ApiParam(name = "Id of the event")
            @PathVariable("id") Long id) {
        Event event = service.findById(id);
        return ResponseEntity.ok(mapper.toDTO(event));
    }

    public static LocalDate convertToLocalDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        List<Event> events = service.findBetweenDates(from, to);
        return ResponseEntity.ok(mapper.toDTOs(events));
    }

    @GetMapping("/upcoming")
    public List<EventDTO> getUpcomingEvents(@RequestParam(required = false, defaultValue = "false") boolean editable) {

        List<Event> events = service.findUpcoming();
        return mapper.toDTOs(events);
    }

    @GetMapping("/past")
    public Stream<EventDTO> getPastEvents(@RequestParam(required = false, defaultValue = "false") boolean editable) {
        User authedUser = getPrincipal();
        List<Event> events = service.findAll();

        Predicate<Event> predicate = event -> {
            if (!event.isVisible() && !editable) {
                return false;
            }
            if (editable) {
                return true;
//                return event.canEdit(authedUser) && event.getStartTime().isBefore(LocalDateTime.now());
            }
            return event.isVisible() && event.getStartTime().isBefore(LocalDateTime.now());
        };

        Stream<Event> filteredEvents = events.stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Event::getStartTime).reversed())
                .limit(30);

        return mapper.toDTOs(filteredEvents);
    }

    @GetMapping("/pageable")
    public Page<EventDTO> getEventsPageable(Pageable pageable) {
        Page<Event> events = service.findAll(pageable);
        return mapper.toDTOs(events);
    }

    @PreAuthorize("hasPermission(#eventId, 'Event', 'delete')")
    @DeleteMapping("/{eventId}")
    public void deleteEventById(@PathVariable("eventId") Long eventId) throws IOException {
        service.deleteEvent(eventId);
    }

    @PreAuthorize("hasPermission(#eventId, 'Event', 'edit')")
    @PutMapping("/{eventId}")
    public EventDTO updateEvent(@PathVariable("eventId") Long eventId, @Valid @RequestBody EventDTO dto) throws IOException {
        Event event = mapper.fromDTO(dto);
        event.setId(eventId);
        Event updatedEvent = service.updateEvent(event);
        return mapper.toDTO(updatedEvent);
    }
}
