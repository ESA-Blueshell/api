package net.blueshell.api.business.news;

import net.blueshell.api.business.user.User;
import net.blueshell.api.constants.StatusCodes;
import net.blueshell.api.controller.AuthorizationController;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.Role;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        Page<News> newsPag = newsRepository.findAll(pageable);
        return newsPag.map(this::from);
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
    public Object createNews(@RequestBody NewsDTO newsDTO) {
        User authedUser = getPrincipal();
        News news = newsDTO.toNews();
        if (!news.canEdit(authedUser)) {
            return StatusCodes.FORBIDDEN;
        }
        news.setAuthor(authedUser);
        news.setPostedAt(Timestamp.valueOf(LocalDateTime.now()));
        news.setLastEditor(authedUser);
        dao.create(news);
        return news;
    }

    @PreAuthorize("hasAuthority('BOARD')")
    @PutMapping(value = "/news/{id}")
    public Object createOrUpdateNews (News news) {
        return StatusCodes.OK;
    }

    @GetMapping(value = "/news/{id}")
    public Object getNewsById(
            @PathVariable(name = "id")
                    String id) {
        News news = dao.getById(Long.parseLong(id));
        if (news == null) {
            return StatusCodes.NOT_FOUND;
        }
        NewsDTO newsDTO = from(dao.getById(Long.parseLong(id)));
        return newsDTO;
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
