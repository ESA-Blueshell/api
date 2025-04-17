package net.blueshell.api.repository;

import net.blueshell.db.BaseRepository;
import net.blueshell.api.model.Committee;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommitteeRepository extends BaseRepository<Committee, Long> {
    List<Committee> findAllByUsersIdEquals(Long userId);
}
