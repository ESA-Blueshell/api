package net.blueshell.api.repository;

import net.blueshell.api.base.BaseRepository;
import net.blueshell.api.model.File;
import net.blueshell.api.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends BaseRepository<File, Long> {
    Optional<File> findByName(String name);
}
