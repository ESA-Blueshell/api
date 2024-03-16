package net.blueshell.api.business.news;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.AbstractDAO;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class NewsDao extends AbstractDAO<News> {

    public NewsDao() {

    }
}
