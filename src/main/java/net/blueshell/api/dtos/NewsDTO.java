package net.blueshell.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.News;
import net.blueshell.api.model.User;
import org.bouncycastle.util.Times;

import java.sql.Timestamp;

public class NewsDTO {

    public String getId() {
        return id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getLast_editor_id() {
        return last_editor_id;
    }

    public void setLast_editor_id(String last_editor_id) {
        this.last_editor_id = last_editor_id;
    }

    public String getLast_editor_username() {
        return last_editor_username;
    }

    public void setLast_editor_username(String last_editor_username) {
        this.last_editor_username = last_editor_username;
    }

    public String getNews_type() {
        return news_type;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(String posted_at) {
        this.posted_at = posted_at;
    }

    UserDao userDao = new UserDao();

    @JsonProperty("id")
    private String id;

    @JsonProperty("creator_id")
    private String creator_id;

    @JsonProperty("creator_username")
    private String creator_username;

    @JsonProperty("last_editor_id")
    private String last_editor_id;

    @JsonProperty("last_editor_username")
    private String last_editor_username;

    @JsonProperty("news_type")
    private String news_type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("posted_at")
    private String posted_at;

    public NewsDTO(String id, String creator_id, String creator_username, String last_editor_id, String last_editor_username, String news_type, String title, String content, String posted_at) {
        this.id = id;
        this.creator_id = creator_id;
        this.creator_username = creator_username;
        this.last_editor_id = last_editor_id;
        this.last_editor_username = last_editor_username;
        this.news_type = news_type;
        this.title = title;
        this.content = content;
        this.posted_at = posted_at;
    }

    //TODO: fix this when we work with POST (the id part)
    public News toNews() {
        User author = userDao.getById(Integer.parseInt(creator_id));
        User last_editor = userDao.getById(Integer.parseInt(last_editor_id));
        Timestamp posted_at = Timestamp.valueOf(this.posted_at);
        return new News(author, news_type, title, content, posted_at);
    }

}
