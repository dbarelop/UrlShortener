package urlshortener.common.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private String sponsor;
	private Date created;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private String ip;
	private String country;
	private Integer status;
	private String badStatusDate;
	private URI qrLink;

	public ShortURL(String hash, String target, URI uri, String sponsor,
			Date created, String owner, Integer mode, Boolean safe, String ip,
			String country) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.ip = ip;
		this.country = country;
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

	public Integer getMode() {
		return mode;
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
	
	public Integer getStatus() {
		return status;
	}
	
	public String getBadStateDate() {
		return badStatusDate;
	}

	public URI getQRLink() {
		return qrLink;
	}

	public void setQRLink(URI qrLink) {
		System.out.println("setQRLink " + qrLink);
		this.qrLink = qrLink;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void setBadStatusDate(String badStatusDate) {
		this.badStatusDate = badStatusDate;
	}

}
