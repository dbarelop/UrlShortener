package urlshortener.team.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    
	@Test
	public void a_testStatusOK() throws Exception {
		postBrandedLink("https://www.facebook.com/","facebook");
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/facebook", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), is(new URI("https://www.facebook.com/")));
	}
	
	@Test
	public void testStatusNull() throws Exception {
		postBrandedLink("https://www.facebok.com/","id1");
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/id1", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), is(new URI("http://localhost:" + this.port + "/404/id1")));
	}

	@Test
	public void z_testCacheServing() throws Exception {
		Thread.sleep(11*1000);
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/cache/facebook", String.class);
		assertTrue(response.hasBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getHeaders().getLocation(), is(nullValue()));
	}
	
	private ResponseEntity<String> postBrandedLink(String url, String id) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("url", url); parts.add("shortName", id);
		return new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/brandedLink", parts, String.class);
	}
}