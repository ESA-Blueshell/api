package net.blueshell.api.repository;

import net.blueshell.db.BaseRepository;
import net.blueshell.api.model.CommitteeMember;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeMemberRepository extends BaseRepository<CommitteeMember, Long> {
}
