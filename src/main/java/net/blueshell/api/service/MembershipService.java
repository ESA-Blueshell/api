package net.blueshell.api.service;

import jakarta.ws.rs.NotFoundException;
import net.blueshell.db.BaseModel;
import net.blueshell.api.model.*;
import net.blueshell.api.repository.MemberRepository;
import net.blueshell.db.BaseModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipService extends BaseModelService<Membership, Long, MemberRepository> {

    @Autowired
    public MembershipService(MemberRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Membership findBySignature(File signature) {
        return repository.findBySignature(signature).orElseThrow(() ->
                new NotFoundException("Member not found for signature: " + signature.getName()));
    }
}
