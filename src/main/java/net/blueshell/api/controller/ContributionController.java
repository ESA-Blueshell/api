package net.blueshell.api.controller;

import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import net.blueshell.api.base.BaseController;
import net.blueshell.api.common.constants.StatusCodes;
import net.blueshell.api.dto.ContributionDTO;
import net.blueshell.api.mapping.ContributionMapper;
import net.blueshell.api.model.Contribution;
import net.blueshell.api.model.ContributionPeriod;
import net.blueshell.api.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import java.util.List;
import java.util.stream.Stream;

@RestController
public class ContributionController extends BaseController<ContributionService, ContributionMapper> {

    @Autowired
    public ContributionController(ContributionService service, ContributionMapper mapper) {
        super(service, mapper);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping("/contributions")
    public ContributionDTO create(@Valid @RequestBody ContributionDTO dto) throws ApiException {
        Contribution contribution = mapper.fromDTO(dto);
        service.createContribution(contribution);
        return mapper.toDTO(contribution);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping("/contributions")
    public Stream<ContributionDTO> getAll(@RequestParam(required = false) Long contributionPeriodId) {
        List<Contribution> contributions = service.findByContributionPeriodId(contributionPeriodId);
        return mapper.toDTOs(contributions.stream());
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping("/contributions/{id}")
    public void delete(@ApiParam(name = "Id of the contribution") @PathVariable("id") Long id) throws ApiException {
        Contribution contribution = service.findById(id);
        service.deleteContribution(contribution);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping("contribution_periods/{periodId}/contributions/remind")
    public ResponseEntity<Object> sendContributionReminder(@PathVariable("periodId") Long periodId) throws ApiException {
        List<Contribution> unpaidContributions = service.findByContributionPeriodIdAndPaid(periodId, false);
        service.sendReminder(unpaidContributions);
        return StatusCodes.OK;
    }
}
