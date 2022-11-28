package net.blueshell.api.business.event;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long>{

    @Query(value = "select e from Event e order by e.startTime desc ")
    @Override
    @NotNull
    Page<Event> findAll(@NotNull Pageable pageable);
}
