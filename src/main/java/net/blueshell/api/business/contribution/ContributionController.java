package net.blueshell.api.business.contribution;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.service.BrevoContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;

@RestController
public class ContributionController extends AuthorizationController {

    @Autowired
    private ContributionDao contributionDao;

    @Autowired
    private BrevoContactService brevoContactService;

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping(value = "/contributions/{id}")
    public ResponseEntity<Object> updateContributionPeriod(@ApiParam(name = "Id of the user") @PathVariable("id") String id,
                                                           @ApiParam(name = "To mark as paid") @QueryParam("paid") Boolean paid) {
        Contribution contribution = contributionDao.getById(Long.parseLong(id));
        if (contribution == null) {
            return StatusCodes.NOT_FOUND;
        }
        contribution.setPaid(paid);
        contributionDao.update(contribution);
        User user = brevoContactService.setInBrevo(contribution.getUser());

        if (user.isInBrevo()){
            if (paid) {
                brevoContactService.addToPaidList(contribution.getContributionPeriod(), contribution.getUser());
            } else {
                brevoContactService.removeFromPaidList(contribution.getContributionPeriod(), contribution.getUser());
            }
        }

        return ResponseEntity.ok().build();
    }
}
