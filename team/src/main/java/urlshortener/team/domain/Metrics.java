package urlshortener.team.domain;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import urlshortener.common.domain.ShortURL;

import java.net.URI;
import java.sql.Date;
import java.util.Map;

public class Metrics {

    private URI uri;
    private Date created;
    private String target;
    private Long clicks;
    private Long uniqueVisitors;
    private Map<Browser, Long> clicksByBrowser;
    private Map<OperatingSystem, Long> clicksByOS;

    public Metrics(URI uri, ShortURL shortURL, Long clicks, Long uniqueVisitors, Map<Browser, Long> clicksByBrowser, Map<OperatingSystem, Long> clicksByOS) {
        this.uri = uri;
        this.created = shortURL.getCreated();
        this.target = shortURL.getTarget();
        this.clicks = clicks;
        this.uniqueVisitors = uniqueVisitors;
        this.clicksByBrowser = clicksByBrowser;
        this.clicksByOS = clicksByOS;
    }

    public Metrics(URI uri, Date created, String target, Long clicks, Long uniqueVisitors, Map<Browser, Long> clicksByBrowser, Map<OperatingSystem, Long> clicksByOS) {
        this.uri = uri;
        this.created = created;
        this.target = target;
        this.clicks = clicks;
        this.uniqueVisitors = uniqueVisitors;
        this.clicksByBrowser = clicksByBrowser;
        this.clicksByOS = clicksByOS;
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

    public Map<Browser, Long> getClicksByBrowser() {
        return clicksByBrowser;
    }

    public void setClicksByBrowser(Map<Browser, Long> clicksByBrowser) {
        this.clicksByBrowser = clicksByBrowser;
    }

    public Map<OperatingSystem, Long> getClicksByOS() {
        return clicksByOS;
    }

    public void setClicksByOS(Map<OperatingSystem, Long> clicksByOS) {
        this.clicksByOS = clicksByOS;
    }
}
