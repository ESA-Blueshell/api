package net.blueshell.api.business.user;

import net.blueshell.api.business.contribution.ContributionPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ContributionPeriod, Long> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.memberType != 'HONORARY' " +
            "AND u.id NOT IN (" +
            "    SELECT c.user.id " +
            "    FROM Contribution c " +
            "    WHERE c.contributionPeriod.id = :contributionPeriodId" +
            ")")
    List<User> getAllUnpaidMembers(@Param("contributionPeriodId") Long contributionPeriodId);
}
