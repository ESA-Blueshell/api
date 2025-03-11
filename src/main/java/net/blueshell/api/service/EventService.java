package net.blueshell.api.service;

import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.dto.EventDTO;
import net.blueshell.api.mapping.EventMapper;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.File;
import net.blueshell.api.model.User;
import net.blueshell.api.repository.EventRepository;
import net.blueshell.api.service.google.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService extends BaseModelService<Event, Long, EventRepository> {

    private final CalendarService calendarService;

    @Autowired
    public EventService(EventRepository repository,
                        CalendarService calendarService) {
        super(repository);
        this.calendarService = calendarService;
    }

    public List<Event> findUpcoming() {
        return repository.findUpcoming();
    }

    /**
     * Creates a new event.
     *
     * @return Created Event entity.
     * @throws IOException if calendar integration fails.
     */
    @Transactional
    public Event createEvent(Event event) throws IOException {

        // Handle visibility and calendar integration
        if (event.isVisible()) {
            calendarService.addToGoogleCalendar(event);
        }

        // Save event to database
        return repository.save(event);
    }

    /**
     * Updates an existing event.
     *
     * @return Updated Event entity.
     * @throws IOException if calendar integration fails.
     */
    @Transactional
    public Event updateEvent(Event event) throws IOException {
        // Handle visibility and calendar integration
        if (event.isVisible()) {
            if (event.getGoogleId() != null) {
                calendarService.updateGoogleCalendar(event);
            } else {
                calendarService.addToGoogleCalendar(event);
            }
        } else if (event.getGoogleId() != null) {
            // Remove from calendar if no longer visible
            calendarService.removeFromGoogleCalendar(event);
        }

        // Save updated event to database
        return repository.save(event);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param id Event ID.
     * @throws IOException if calendar integration fails.
     */

    @Transactional
    public void deleteEvent(Long id) throws IOException {
        Event event = findById(id);

        // Remove from calendar if present
        if (event.getGoogleId() != null) {
            calendarService.removeFromGoogleCalendar(event);
        }

        repository.delete(event);
    }

    @Override
    protected Long extractId(Event event) {
        return event.getId();
    }

    public Event findByBanner(File banner) {
        return repository.findByBanner(banner);
    }

    public List<Event> findStartTimeBetween(LocalDateTime from, LocalDateTime to) {
        return repository.findStartTimeBetween(from, to);
    }
}
