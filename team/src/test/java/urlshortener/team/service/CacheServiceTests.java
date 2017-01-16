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
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/hash0", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), is(new URI("https://moodle2.unizar.es/add/")));
	}
	
	@Test
	public void testStatusNull() throws Exception {
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/hash5", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(response.getHeaders().getLocation(), is(new URI("http://localhost:" + this.port + "/404/hash5")));
	}

	@Test
	public void z_testCacheServing() throws Exception {
		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/cache/hash0", String.class);
		assertTrue(response.hasBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getHeaders().getLocation(), is(nullValue()));
	}
}