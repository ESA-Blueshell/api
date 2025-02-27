package net.blueshell.api.repository;

import net.blueshell.api.base.BaseRepository;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.File;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends BaseRepository<Event, Long> {

    @NotNull
    @Query("SELECT e FROM Event e ORDER BY e.startTime DESC")
    @Override
    Page<Event> findAll(@NotNull Pageable pageable);

    Event findByBanner(File banner);
}
