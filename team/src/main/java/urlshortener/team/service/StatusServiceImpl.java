package urlshortener.team.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.domain.CachedPage;
import urlshortener.team.repository.CachedPageRepository;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class StatusServiceImpl implements StatusService {

	private static final Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);
	private static final long CHECK_PERIOD = 60 * 1000;		// 1 minute

	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private CachedPageRepository cachedPageRepository;

	@Override
	@Scheduled(fixedRate = CHECK_PERIOD)
	public void periodicallyVerifyStatus() {
		logger.debug("Periodical URL status verification starting...");
		List<ShortURL> listVerifyURL = shortURLRepository.listVerify();
		for (ShortURL su : listVerifyURL) {
			verifyStatus(su);
		}
	}

	@Override
	public CachedPage getCachedPage(String hash) {
		return cachedPageRepository.findById(hash);
	}

	@Override
	@Async
	public void verifyStatus(ShortURL shortURL) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.exchange(shortURL.getTarget(), HttpMethod.GET, null, String.class);
		shortURL.setLastStatus(result.getStatusCode());
		shortURL.setLastCheck(new Date());
		if (result.getStatusCode() == HttpStatus.OK) {
			cacheStaticPage(result, shortURL);
		}
	}
	
	private void cacheStaticPage(ResponseEntity<String> result, ShortURL shortURL) {
		CachedPage cachedPage = new CachedPage(shortURL.getHash(), shortURL.getLastCheck(), shortURL.getTarget(), result.getBody());
		cachedPageRepository.save(cachedPage);
	}
}
