package urlshortener.team.domain;

import urlshortener.common.domain.ShortURL;

import java.net.URI;
import java.sql.Date;

public class Metrics {

    private URI uri;
    private Date created;
    private String target;
    private Long clicks;

    public Metrics(URI uri, ShortURL shortURL, Long clicks) {
        this.uri = uri;
        this.created = shortURL.getCreated();
        this.target = shortURL.getTarget();
        this.clicks = clicks;
    }

    public Metrics(URI uri, Date created, String target, Long clicks) {
        this.uri = uri;
        this.created = created;
        this.target = target;
        this.clicks = clicks;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }
}
