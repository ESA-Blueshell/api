package net.blueshell.api.business.news;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class NewsDao extends SessionWrapper<News> implements Dao<News> {

    public NewsDao() { super(News.class); }
}
