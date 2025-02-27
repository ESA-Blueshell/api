package net.blueshell.api.controller;

import jakarta.validation.Valid;
import net.blueshell.api.base.BaseController;
import net.blueshell.api.common.constants.StatusCodes;
import net.blueshell.api.dto.ContributionPeriodDTO;
import net.blueshell.api.mapping.ContributionPeriodMapper;
import net.blueshell.api.model.ContributionPeriod;
import net.blueshell.api.service.ContributionPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sendinblue.ApiException;

import java.util.List;

@RestController
@RequestMapping("/contributionPeriods")
public class ContributionPeriodController extends BaseController<ContributionPeriodService, ContributionPeriodMapper> {

    @Autowired
    public ContributionPeriodController(ContributionPeriodService service,
                                        ContributionPeriodMapper mapper) {
        super(service, mapper);
    }

    @GetMapping
    public List<ContributionPeriod> getContributionPeriods() {
        return service.findAll();
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PostMapping
    public ResponseEntity<ContributionPeriodDTO> createContributionPeriod(@Valid @RequestBody ContributionPeriodDTO dto) throws ApiException {
        ContributionPeriod contributionPeriod = mapper.fromDTO(dto);
        service.createContributionPeriod(contributionPeriod);
        return ResponseEntity.ok(mapper.toDTO(contributionPeriod));
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping("/{id}")
    public ResponseEntity<ContributionPeriodDTO> updateContributionPeriod(@PathVariable("id") Long id,
                                                                          @Valid @RequestBody ContributionPeriodDTO dto) {
        ContributionPeriod contributionPeriod = mapper.fromDTO(dto);
        contributionPeriod = service.update(contributionPeriod);
        return ResponseEntity.ok(mapper.toDTO(contributionPeriod));
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteContributionPeriod(@PathVariable("id") Long id) {
        ContributionPeriod contributionPeriod = service.findById(id);
        service.deleteContributionPeriod(contributionPeriod);
        return ResponseEntity.ok().build();
    }
}
