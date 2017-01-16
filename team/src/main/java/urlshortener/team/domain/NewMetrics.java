package urlshortener.team.domain;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import urlshortener.common.domain.Click;

import java.util.Date;

public class NewMetrics {

    private String hash;
    private Date date;
    private Date lastVisitDate;
    private Browser browser;
    private OperatingSystem os;

    public NewMetrics(String hash, Date date, Date lastVisitDate, Browser browser, OperatingSystem os) {
        this.hash = hash;
        this.date = date;
        this.lastVisitDate = lastVisitDate;
        this.browser = browser;
        this.os = os;
    }

    public NewMetrics(Click click) {
        this.hash = click.getHash();
        this.date = click.getCreated();
        this.lastVisitDate = null;  // NOTE: complete outside (depends on repository)
        this.browser = Browser.valueOf(click.getBrowser());
        this.os = OperatingSystem.valueOf(click.getPlatform());
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public OperatingSystem getOs() {
        return os;
    }

    public void setOs(OperatingSystem os) {
        this.os = os;
    }
}
