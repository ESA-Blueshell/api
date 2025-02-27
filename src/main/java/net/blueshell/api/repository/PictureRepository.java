package net.blueshell.api.repository;

import net.blueshell.api.base.BaseRepository;
import net.blueshell.api.model.Picture;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends BaseRepository<Picture, Long> {
}
