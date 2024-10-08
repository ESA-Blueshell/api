package net.blueshell.api.business.contribution;

import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.service.BrevoContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ContributionPeriodController extends AuthorizationController {

    @Autowired
    private ContributionPeriodDao contributionPeriodDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private ContributionDao contributionDao;
    @Autowired
    private BrevoContactService brevoContactService;

    @GetMapping(value = "/contributionPeriods")
    public List<ContributionPeriod> getContributionPeriods() {
        return contributionPeriodDao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/contributionPeriods")
    public ResponseEntity<Object> createContributionPeriod(@RequestBody ContributionPeriod contributionPeriod) {
        brevoContactService.createListForContributionPeriod(contributionPeriod);
        contributionPeriod = contributionPeriodDao.create(contributionPeriod);
        // Get the list of users
        List<User> users = userDao.list();

        // Create a list of contributions
        List<Contribution> contributions = new ArrayList<>();
        for (User user : users) {
            contributions.add(new Contribution(user, contributionPeriod));
        }

        // Batch insert all contributions
        contributionDao.createAll(contributions);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/contributionPeriods/{id}")
    public ResponseEntity<Object> updateContributionPeriod(@PathVariable("id") Long id, @RequestBody ContributionPeriod contributionPeriod) {
        ContributionPeriod existingPeriod = contributionPeriodDao.getById(id);
        if (existingPeriod == null) {
            return StatusCodes.NOT_FOUND;
        }
        existingPeriod.setStartDate(contributionPeriod.getStartDate());
        existingPeriod.setEndDate(contributionPeriod.getEndDate());
        existingPeriod.setHalfYearFee(contributionPeriod.getHalfYearFee());
        existingPeriod.setFullYearFee(contributionPeriod.getFullYearFee());
        existingPeriod.setAlumniFee(contributionPeriod.getAlumniFee());
        contributionPeriodDao.update(existingPeriod);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "contributionPeriods/{id}")
    public ResponseEntity<Object> deleteContributionPeriod(@PathVariable("id") Long id) {
        ContributionPeriod contributionPeriod = contributionPeriodDao.getById(id);
        if (contributionPeriod == null) {
            return StatusCodes.NOT_FOUND;
        }
        contributionDao.deleteByContributionPeriod(contributionPeriod);
        contributionPeriodDao.delete(id);
        return ResponseEntity.ok().build();
    }
}
