package urlshortener.team.domain;

import org.springframework.data.annotation.Id;
import urlshortener.common.domain.ShortURL;

import java.util.Date;

public class CachedPage {
    @Id
    private String id;
    private Date date;
    private String target;
    private String body;

    public CachedPage() {
    }

    public CachedPage(String id, Date date, String target, String body) {
        this.id = id;
        this.date = date;
        this.target = target;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
