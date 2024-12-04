package net.blueshell.api.business.contribution;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.payment.PaymentDao;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.service.BrevoContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

@RestController
public class ContributionController extends AuthorizationController {

    @Autowired
    private ContributionDao contributionDao;

    @Autowired
    private BrevoContactService brevoContactService;

    @Autowired
    private PaymentDao paymentDao;

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/contributions")
    public ResponseEntity<Object> create(@RequestBody Contribution contribution) {
        if (contribution == null) {
            return StatusCodes.BAD_REQUEST;
        }

        contributionDao.create(contribution);
        return ResponseEntity.ok(contribution);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/contributions/{id}")
    public ResponseEntity<Object> delete(@ApiParam(name = "Id of the contribution") @PathVariable("id") String id) {
        Contribution contribution = contributionDao.getById(Long.parseLong(id));
        if (contribution == null) {
            return StatusCodes.NOT_FOUND;
        }

        try {
            brevoContactService.createOrUpdateContact(contribution.getUser());
            brevoContactService.removeFromContributionPeriodList(contribution.getContributionPeriod(), contribution.getUser());
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(e.getResponseBody());
        }

        contributionDao.delete(contribution.getId());
        return ResponseEntity.ok().build();
    }
}
