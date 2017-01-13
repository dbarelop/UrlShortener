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
	private Date lastCheck;
	private Date availableSince;
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

	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public Date getCreated() {
		return created;
	}

	public String getOwner() {
		return owner;
	}

	public String getSponsor() {
		return sponsor;
	}

	public Boolean getSafe() {
		return safe;
	}

	public String getIP() {
		return ip;
	}

	public String getCountry() {
		return country;
	}
	
	public HttpStatus getLastStatus() {
		return lastStatus;
	}
	
	public Date getLastCheck() {
		return lastCheck;
	}
	
	public Date getAvailableSince() {
		return availableSince;
	}

	public URI getQRLink() {
		return qrLink;
	}

	public void setQRLink(URI qrLink) {
		this.qrLink = qrLink;
	}
	
	public void setLastStatus(HttpStatus lastStatus) {
		this.lastStatus = lastStatus;
	}
	
	public void setLastCheck(Date lastCheck) {
		this.lastCheck = lastCheck;
	}
	
	public void setAvailableSince(Date availableSince) {
		this.availableSince = availableSince;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
