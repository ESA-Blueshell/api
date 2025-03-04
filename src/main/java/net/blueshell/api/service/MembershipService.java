package net.blueshell.api.service;

import jakarta.ws.rs.NotFoundException;
import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.model.*;
import net.blueshell.api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipService extends BaseModelService<Member, Long, MemberRepository> {

    @Autowired
    public MembershipService(MemberRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Member findBySignature(File signature) {
        return repository.findBySignature(signature).orElseThrow(() ->
                new NotFoundException("Member not found for signature: " + signature.getName()));
    }

    @Override
    protected Long extractId(Member entity) {
        return entity.getId();
    }
}
