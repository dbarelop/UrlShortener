package urlshortener.team.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.RuleRepository;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.repository.fixture.ShortURLFixture;
import urlshortener.team.repository.fixture.VerificationRuleFixture;

import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class CacheServiceTests {

	@Value("${local.server.port}")
	private int port = 0;
	@Autowired
    private RuleRepository ruleRepository;
	@Autowired
    private ShortURLRepository shortURLRepository;

	@Test
	public void testStatusOK() throws Exception {
		ShortURL shortURL = shortURLRepository.save(ShortURLFixture.url4());
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/" + shortURL.getHash(), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), is(new URI("https://www.facebook.com/")));
	}
	
	@Test
	public void testStatusNull() throws Exception {
		ShortURL shortURL = shortURLRepository.save(ShortURLFixture.url3());
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/" + shortURL.getHash(), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), 
				is(new URI("http://localhost:" + this.port + "/404/" + shortURL.getHash())));
	}

	@Test
	public void testRulesInvalid() throws Exception {
		ShortURL shortURL = shortURLRepository.save(ShortURLFixture.url2());
		Thread.sleep(5*1000);
		ruleRepository.save(VerificationRuleFixture.rule2());
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/" + shortURL.getHash(), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), 
				is(new URI("http://localhost:" + this.port + "/404/" + shortURL.getHash())));
	}

	@Test
	public void testStatusBadAndCacheServing() throws Exception {
		ShortURL shortURL = shortURLRepository.save(ShortURLFixture.url5());
		Thread.sleep(11*1000);
		shortURL.setLastStatus(null);
		shortURLRepository.update(shortURL);
		ResponseEntity<String> response1 = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/" + shortURL.getHash(), String.class);
		ResponseEntity<String> response2 = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/cache/" + shortURL.getHash(), String.class);
		
		assertThat(response1.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response1.getHeaders().getLocation(), 
				is(new URI("http://localhost:" + this.port + "/404/" + shortURL.getHash())));
		assertTrue(response2.hasBody());
		assertThat(response2.getStatusCode(), is(HttpStatus.OK));
	}
}