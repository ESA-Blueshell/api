package net.blueshell.api.business.picture;

import net.blueshell.api.db.AbstractDAO;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class PictureDao extends AbstractDAO<Picture>
{

    public PictureDao() {
        super(Picture.class);
    }
}
