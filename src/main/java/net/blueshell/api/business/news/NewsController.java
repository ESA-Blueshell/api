package net.blueshell.api.business.news;

import com.google.j2objc.annotations.AutoreleasePool;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NewsController extends AuthorizationController {

    private final Dao<News> dao = new NewsDao();
    private final UserDao userDao = new UserDao();

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping(value = "/newsPageable")
    Page newsPageable(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @GetMapping(value = "/news")
    public List<NewsDTO> getNews() {
        // https://stackoverflow.com/questions/7221833/how-can-i-call-a-method-on-each-element-of-a-list/20684006#20684006 have fun
        List<NewsDTO> newsList = dao.list().stream().map(this::from).collect(Collectors.toList());
        // Collections.reverse didn't work
        for (int i = 0, j = newsList.size() - 1; i < j; i++) {
            newsList.add(i, newsList.remove(j));
        }
        return newsList;
    }



    @PreAuthorize("hasAuthority('BOARD')")
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

    @GetMapping(value = "/news/{id}")
    public Object getNewsById(
            @PathVariable(name = "id")
                    String id) {
        NewsDTO news = from(dao.getById(Long.parseLong(id)));
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



    public NewsDTO from(News news) {
        return new NewsDTO(String.valueOf(news.getId()), String.valueOf(news.getAuthorId()), userDao.getById(news.getAuthorId()).getUsername(),
                String.valueOf(news.getLastEditorId()), userDao.getById(news.getLastEditorId()).getUsername(), news.getNewsType(),
                news.getTitle(), news.getContent(), news.getPostedAt().toString());
    }
}
