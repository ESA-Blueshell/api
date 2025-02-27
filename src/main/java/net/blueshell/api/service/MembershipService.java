package net.blueshell.api.service;

import jakarta.ws.rs.NotFoundException;
import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.*;
import net.blueshell.api.repository.CommitteeRepository;
import net.blueshell.api.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipService extends BaseModelService<Membership, Long, MembershipRepository> {

    @Autowired
    public MembershipService(MembershipRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Membership findBySignature(File signature) {
        return repository.findBySignature(signature).orElseThrow(() ->
                new NotFoundException("Membership not found for signature: " + signature.getName()));
    }

    @Override
    protected Long extractId(Membership entity) {
        return entity.getId();
    }
}
