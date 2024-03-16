package net.blueshell.api.business.news;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@DependsOn("dataSource")
public class NewsDao extends AbstractDAO<News> {

    public NewsDao() {

    }

	public Collection<News> list()
	{

		throw new NotYetImplementedException();
	}

	public News create(News news)
	{

		throw new NotYetImplementedException();
	}

	public News getById(long id)
	{

		throw new NotYetImplementedException();
	}

	public void update(News nw)
	{

		throw new NotYetImplementedException();
	}

	public void delete(long l)
	{

		throw new NotYetImplementedException();
	}
}
