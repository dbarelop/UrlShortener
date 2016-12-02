package urlshortener.team.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StatusService {
	private static final Logger logger = LoggerFactory.getLogger(StatusService.class);
	private Integer status;
	private String badStatusDate;
	
	@Async
	public void verifyStatus(String url) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, 
					null, String.class);
			status = result.getStatusCodeValue();
			logger.info("Status: " + status + " from uri = " + url);
		} catch (Exception e) {
			status = 400;
			badStatusDate();
			logger.info("Status: " + status + " from uri = " + url);
		}
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public String getBadStatusDate() {
		return badStatusDate;
	}
	
	private void badStatusDate() {
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        Date badStatus = new Date();
        badStatusDate = SDF.format(badStatus);
    }
}
