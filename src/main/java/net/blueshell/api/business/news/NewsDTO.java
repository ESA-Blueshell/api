package net.blueshell.api.business.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.User;

import java.sql.Timestamp;

public class NewsDTO {

    UserDao userDao = new UserDao();

    @JsonProperty("id")
    private String id;

    @JsonProperty("creatorId")
    private String creatorId;

    @JsonProperty("creatorUsername")
    private String creatorUsername;

    @JsonProperty("lastEditorId")
    private String lastEditorId;

    @JsonProperty("lastEditorUsername")
    private String lastEditorUsername;

    @JsonProperty("newsType")
    private String newsType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("postedAt")
    private String postedAt;

    public NewsDTO(String id, String creatorId, String creatorUsername, String lastEditorId, String lastEditorUsername, String newsType, String title, String content, String postedAt) {
        this.id = id;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.lastEditorId = lastEditorId;
        this.lastEditorUsername = lastEditorUsername;
        this.newsType = newsType;
        this.title = title;
        this.content = content;
        this.postedAt = postedAt;
    }

    public String getId() {
        return id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(String lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public String getLastEditorUsername() {
        return lastEditorUsername;
    }

    public void setLastEditorUsername(String lastEditorUsername) {
        this.lastEditorUsername = lastEditorUsername;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
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

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    //TODO: fix this when we work with POST (the id part)
    public News toNews() {
        User author = userDao.getById(Integer.parseInt(creatorId));
        User last_editor = userDao.getById(Integer.parseInt(lastEditorId));
        Timestamp posted_at = Timestamp.valueOf(this.postedAt);
        return new News(author, newsType, title, content, posted_at);
    }

}
