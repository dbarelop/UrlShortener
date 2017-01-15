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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.CachedPage;
import urlshortener.team.domain.VerificationRule;
import urlshortener.team.repository.CachedPageRepository;
import urlshortener.team.repository.RuleRepository;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class CacheServiceImpl implements CacheService {

	private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
	private static final long CHECK_PERIOD = 10 * 1000;		// 10 seconds

	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private CachedPageRepository cachedPageRepository;
	@Autowired
	private RuleRepository ruleRepository;

	@Override
	@Scheduled(fixedRate = CHECK_PERIOD)
	public void periodicallyVerifyStatus() {
		logger.debug("Periodical URL status verification starting...");
		List<ShortURL> shortURLs = shortURLRepository.findAll();
		if (shortURLs != null) {
			for (ShortURL su : shortURLs) {
				verifyStatus(su);
			}
		}
	}

	@Override
	public CachedPage getCachedPage(String hash) {
		return cachedPageRepository.findById(hash);
	}

	@Override
	public boolean isCached(String hash) {
		return cachedPageRepository.exists(hash);
	}

	@Override
	@Async
	public void verifyStatus(ShortURL shortURL) {
		RestTemplate restTemplate = new RestTemplate();
		shortURL.setLastCheckDate(new Date());
		try {
			ResponseEntity<String> result = restTemplate.exchange(shortURL.getTarget(), HttpMethod.GET, null, String.class);
			shortURL.setLastStatus(result.getStatusCode());
			if (result.getStatusCode() == HttpStatus.OK) {
				List<VerificationRule> rules = ruleRepository.findByHash(shortURL.getHash());
				boolean passesRules = rules.parallelStream().allMatch(r -> r.validate(result.getBody()));
				if (passesRules) {
					shortURL.setValid(true);
					shortURL.setCacheDate(new Date());
					cacheStaticPage(result, shortURL);
				} else {
					logger.info("** " + shortURL.getTarget() + " (" + shortURL.getHash() + ") didn't pass the user rules verification");
					shortURL.setValid(false);
				}
			}
		} catch (RestClientException e) {
			logger.info("** " + shortURL.getTarget() + " (" + shortURL.getHash() + ") down");
			shortURL.setLastStatus(null);
		}
		shortURLRepository.update(shortURL);
	}
	
	private void cacheStaticPage(ResponseEntity<String> result, ShortURL shortURL) {
		logger.info("** Storing cache version of " + shortURL.getTarget());
		CachedPage cachedPage = new CachedPage(shortURL.getHash(), shortURL.getCacheDate(), shortURL.getTarget(), result.getBody());
		cachedPageRepository.save(cachedPage);
	}
}
