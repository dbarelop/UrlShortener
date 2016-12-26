package urlshortener.team.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class StatusService {
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	private static final Logger LOG = LoggerFactory.getLogger(StatusService.class);
	//private static final long PERIOD = 3600000; // 1 hour
	private static final long PERIOD = 60000; // 1 minutes
	private Integer status;
	private String badStatusDate;

	@Async
	public void verifyStatus(String url) {
		RestTemplate restTemplate = new RestTemplate();
		status = 200;
		badStatusDate = "";
		try {
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, 
					null, String.class);
			status = result.getStatusCodeValue();
			LOG.info("Status: " + status + " from uri = " + url);
		} catch (Exception e) {
			status = 400;
			badStatusDate();
			LOG.info("Status: " + status + " from uri = " + url);
		}
	}
	
	@Scheduled(fixedRate = PERIOD)
	public void periodicallyVerifyStatus() {
		LOG.info("Periodically Verify Init.");
		List<ShortURL> listVerifyURL = shortURLRepository.listVerify();
		if (listVerifyURL.isEmpty()){
			LOG.info("Short URL repository is empty.");
		}
		else {
			for (ShortURL shortURL : listVerifyURL) {
				String url = shortURL.getTarget();
				String date = shortURL.getBadStateDate();
				verifyStatus(url);
				ShortURL su = new ShortURL(shortURL.getHash(), shortURL.getTarget(), 
						shortURL.getUri(), shortURL.getSponsor(), shortURL.getCreated(), 
						shortURL.getOwner(), shortURL.getMode(), shortURL.getSafe(), 
						shortURL.getIP(), shortURL.getCountry());
				if (status == 400) {
					badStatusDate = date;
				}
				su.setStatus(status);
				su.setBadStatusDate(badStatusDate);
				shortURLRepository.update(su);
				LOG.info("Uri = " + su.getTarget() + " - Status = " +
						su.getStatus() + " since = " + su.getBadStateDate());
			}
		}
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public String getBadStatusDate() {
		return badStatusDate;
	}
	
	private void badStatusDate() {
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date badStatus = new Date();
        badStatusDate = SDF.format(badStatus);
    }
}
