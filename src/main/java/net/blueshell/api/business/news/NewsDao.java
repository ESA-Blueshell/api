package net.blueshell.api.business.news;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class NewsDao extends SessionWrapper<News> implements Dao<News> {

    public NewsDao() { super(News.class); }
}
