package net.blueshell.api.service;

import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Committee;
import net.blueshell.api.model.CommitteeMember;
import net.blueshell.api.model.User;
import net.blueshell.api.repository.CommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitteeService extends BaseModelService<Committee, Long, CommitteeRepository> {

    private static UserService userService;

    @Autowired
    public CommitteeService(CommitteeRepository repository, UserService userService) {
        super(repository);
    }

    @Override
    protected Long extractId(Committee committee) {
        return committee.getId();
    }

    public void createCommittee(Committee committee) {
        repository.save(committee);
        for (CommitteeMember member : committee.getMembers()) {
            User user = member.getUser();
            userService.addRole(user, Role.COMMITTEE);
        }
    }
}
