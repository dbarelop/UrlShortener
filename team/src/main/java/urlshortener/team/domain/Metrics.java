package urlshortener.team.domain;

import urlshortener.common.domain.ShortURL;

import java.net.URI;
import java.sql.Date;

public class Metrics {

    private URI uri;
    private Date created;
    private String target;
    private Long clicks;
    private Long uniqueVisitors;
    private Long differentBrowsers;
    private Long differentOperatingSystems;

    public Metrics(URI uri, ShortURL shortURL, Long clicks, Long uniqueVisitors, Long differentBrowsers, Long differentOperatingSystems) {
        this.uri = uri;
        this.created = shortURL.getCreated();
        this.target = shortURL.getTarget();
        this.clicks = clicks;
        this.uniqueVisitors = uniqueVisitors;
        this.differentBrowsers = differentBrowsers;
        this.differentOperatingSystems = differentOperatingSystems;
    }

    public Metrics(URI uri, Date created, String target, Long clicks, Long uniqueVisitors, Long differentBrowsers, Long differentOperatingSystems) {
        this.uri = uri;
        this.created = created;
        this.target = target;
        this.clicks = clicks;
        this.uniqueVisitors = uniqueVisitors;
        this.differentBrowsers = differentBrowsers;
        this.differentOperatingSystems = differentOperatingSystems;
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

    public Long getUniqueVisitors() {
        return uniqueVisitors;
    }

    public void setUniqueVisitors(Long uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    public Long getDifferentBrowsers() {
        return differentBrowsers;
    }

    public void setDifferentBrowsers(Long differentBrowsers) {
        this.differentBrowsers = differentBrowsers;
    }

    public Long getDifferentOperatingSystems() {
        return differentOperatingSystems;
    }

    public void setDifferentOperatingSystems(Long differentOperatingSystems) {
        this.differentOperatingSystems = differentOperatingSystems;
    }
}
