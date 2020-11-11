package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.News;

public class NewsDao extends SessionWrapper<News> implements Dao<News> {

    public NewsDao() { super(News.class); }
}
