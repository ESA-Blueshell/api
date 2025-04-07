package net.blueshell.api.repository;

import net.blueshell.api.base.BaseRepository;
import net.blueshell.api.model.EventPicture;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventPictureRepository extends BaseRepository<EventPicture, Long> {
}
