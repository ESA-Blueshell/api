package net.blueshell.api.business.sponsor;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("dataSource")
public class SponsorDao extends AbstractDAO<Sponsor>
{

    public SponsorDao() {
    }

    public List<Sponsor> list()
    {

        throw new NotYetImplementedException();
    }

    public void create(Sponsor sponsor)
    {

        throw new NotYetImplementedException();
    }

    public Sponsor getById(long id)
    {

        throw new NotYetImplementedException();
    }

    public void update(Sponsor sponsor)
    {

        throw new NotYetImplementedException();
    }

    public void delete(long l)
    {

        throw new NotYetImplementedException();
    }
}
