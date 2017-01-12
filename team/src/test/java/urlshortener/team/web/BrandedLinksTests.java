package urlshortener.team.web;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= RANDOM_PORT)
@DirtiesContext
public class BrandedLinksTests {

	@Value("${local.server.port}")
	private int port = 0;


	@Test
	public void a_testCreateLink() throws Exception {
		ResponseEntity<String> entity = postBrandedLink("http://example.com/","id");
		assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://localhost:"+ this.port+"/id")));
		assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", Charset.forName("UTF-8"))));
		ReadContext rc = JsonPath.parse(entity.getBody());
		assertThat(rc.read("$.hash"), is("id"));
		assertThat(rc.read("$.uri"), is("http://localhost:"+ this.port+"/id"));
		assertThat(rc.read("$.target"), is("http://example.com/"));
		assertThat(rc.read("$.sponsor"), is(nullValue()));
	}

	@Test
	public void b_testUniqueId() throws Exception {
		ResponseEntity<String> entity = postBrandedLink("http://example.com/server/","id");
		assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	
	@Test
	public void c_testRedirection() throws Exception {
		postBrandedLink("http://example.com/","id1");
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port
						+ "/id1", String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
	}
	
	
	private ResponseEntity<String> postBrandedLink(String url, String id) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("url", url);parts.add("shortName", id);
		return new TestRestTemplate().postForEntity(
				"http://localhost:" + this.port+"/brandedLink", parts, String.class);
	}


}