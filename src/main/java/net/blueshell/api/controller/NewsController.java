package net.blueshell.api.controller;

import com.wordnik.swagger.annotations.ApiParam;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.daos.NewsDao;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.News;
import net.blueshell.api.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NewsController extends AuthorizationController {

    private final Dao<News> dao = new NewsDao();
    private final UserDao userDao = new UserDao();

    @GetMapping(value = "/news")
    public List<News> getNews() {return dao.list();}

    @PreAuthorize(("hasAuthority('BOARD')"))
    @PostMapping(value = "/news")
    public Object createNews(News news) {
        try {
            return dao.create(news);
        } catch (Exception e) {
            e.printStackTrace();
            return StatusCodes.BAD_REQUEST;
        }
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/news/{id}")
    public Object createOrUpdateNews (News news) {
        News nw = dao.getById(news.getId());
        if (nw == null && hasAuthorization(Role.BOARD)) {
            // create new news
            return createNews(news);
        } else {
            dao.update(nw);
        }
        return StatusCodes.OK;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @GetMapping(value = "/news/{id}")
    public Object getNewsById(
            @ApiParam(name = "Id of the news")
            @PathVariable("id") String id) {
        News news = dao.getById(Long.parseLong(id));
        if (news == null) {
            return StatusCodes.NOT_FOUND;
        }
        return news;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @DeleteMapping(value = "/news/{id}")
    public Object deleteNewsById(@PathVariable("id") String id) {
        News news = dao.getById(Long.parseLong(id));
        if(news == null) {
            return StatusCodes.NOT_FOUND;
        }
        dao.delete(Long.parseLong(id));
        return StatusCodes.OK;
    }
}
