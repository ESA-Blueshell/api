package net.blueshell.api.business.committee;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@DependsOn("dataSource")
public class CommitteeDao extends AbstractDAO<Committee> {

    public CommitteeDao() {
    }

	public Collection<Committee> list()
	{

		throw new NotYetImplementedException();
	}

	public Committee create(Committee committee)
	{

		throw new NotYetImplementedException();
	}

	public Committee getById(long l)
	{

		throw new NotYetImplementedException();
	}

	public void update(Committee newCommittee)
	{

		throw new NotYetImplementedException();
	}

	public void delete(long l)
	{

		throw new NotYetImplementedException();
	}
}
