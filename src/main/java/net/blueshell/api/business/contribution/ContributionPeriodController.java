package net.blueshell.api.business.contribution;

import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.UserRepository;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.service.BrevoContactService;
import net.blueshell.api.service.BrevoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

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
    @Autowired
    private BrevoEmailService brevoEmailService;

    @Autowired
    UserRepository userRepository;


    @GetMapping(value = "/contributionPeriods")
    public List<ContributionPeriod> getContributionPeriods() {
        return contributionPeriodDao.list();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/contributionPeriods")
    public ResponseEntity<Object> createContributionPeriod(@RequestBody ContributionPeriod contributionPeriod) {
        try {
            brevoContactService.createContributionPeriodList(contributionPeriod);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(e.getResponseBody());
        }

        contributionPeriod = contributionPeriodDao.create(contributionPeriod);
        return ResponseEntity.ok(contributionPeriod);
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

    @PutMapping(value = "/contributionPeriods/{id}/remind")
    public ResponseEntity<Object> sendContributionReminder(@PathVariable("id") Long id) {
        ContributionPeriod contributionPeriod = contributionPeriodDao.getById(id);
        if (contributionPeriod == null) {
            return StatusCodes.NOT_FOUND;
        }

        List<User> users = userRepository.getAllUnpaidMembers(contributionPeriod.getId());
        brevoEmailService.sendContributionReminderEmail(users, contributionPeriod);

        return StatusCodes.OK;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping("contributionPeriods/{id}/contributions")
    public ResponseEntity<Object> getContributionsByPeriod(@PathVariable Long id) {
        ContributionPeriod period = contributionPeriodDao.getById(id);
        if (period == null) {
            return StatusCodes.NOT_FOUND;
        }
        List<Contribution> contributions = contributionDao.getContributionsByPeriod(period);
        return ResponseEntity.ok(contributions);
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
