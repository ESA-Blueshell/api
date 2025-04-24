package net.blueshell.api.controller;

import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import net.blueshell.dto.ContributionDTO;
import net.blueshell.api.mapping.ContributionMapper;
import net.blueshell.api.model.Contribution;
import net.blueshell.api.service.ContributionService;
import net.blueshell.db.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PutMapping("/contributions/{id}/paid")
    public ContributionDTO paid(
            @ApiParam(name = "Id of the contribution") @PathVariable("id") Long id,
            @ApiParam(name = "Whether the contribution is paid") @PathParam("paid") boolean paid) throws ApiException {
        Contribution contribution = service.findById(id);
        contribution.setPaid(paid);
        service.update(contribution);
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
    @PutMapping("contributionPeriods/{periodId}/contributions/remind")
    public void sendContributionReminder(@PathVariable("periodId") Long periodId) throws ApiException {
        List<Contribution> unpaidContributions = service.findByContributionPeriodIdAndPaid(periodId, false);
        service.sendReminder(unpaidContributions);
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping("contributionPeriods/{periodId}/contributions")
    public List<ContributionDTO> getContributionsByPeriodId(@PathVariable("periodId") Long periodId) throws ApiException {
        List<Contribution> contributions = service.findByContributionPeriodId(periodId);
        return mapper.toDTOs(contributions);
    }
}
