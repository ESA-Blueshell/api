package net.blueshell.api.business.picture;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("dataSource")
public class PictureDao extends AbstractDAO<Picture>
{

    public PictureDao() {
    }

    public List<Picture> list()
    {
        throw new NotYetImplementedException();
    }

    public void create(Picture picture)
    {
        throw new NotYetImplementedException();
    }

    public Picture getById(long id)
    {
        throw new NotYetImplementedException();
    }

    public void update(Picture pic)
    {

        throw new NotYetImplementedException();
    }

    public void delete(long l)
    {

        throw new NotYetImplementedException();
    }
}
