package urlshortener.team.domain;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Date;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private String sponsor;
	private Date created;
	private String owner;
	private Boolean safe;
	private String ip;
	private String country;
	private HttpStatus lastStatus;
	private Date lastCheckDate;
	private Date cacheDate;
	private URI qrLink;
	private String user;

	public ShortURL(String hash, String target, URI uri, String sponsor,
					Date created, String owner, Boolean safe, String ip,
					String country, String user) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
		this.user = user;
		this.lastStatus = HttpStatus.OK;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Boolean getSafe() {
		return safe;
	}

	public void setSafe(Boolean safe) {
		this.safe = safe;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public HttpStatus getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(HttpStatus lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Date getLastCheckDate() {
		return lastCheckDate;
	}

	public void setLastCheckDate(Date lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	public Date getCacheDate() {
		return cacheDate;
	}

	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate;
	}

	public URI getQrLink() {
		return qrLink;
	}

	public void setQrLink(URI qrLink) {
		this.qrLink = qrLink;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
