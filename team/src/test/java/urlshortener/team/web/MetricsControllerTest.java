package urlshortener.team.web;

import com.google.common.collect.Lists;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import urlshortener.common.domain.ShortURL;
import urlshortener.team.domain.Metrics;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class MetricsControllerTest {

    private TestRestTemplate template = new TestRestTemplate();

    @Value("${local.server.port}")
    private int port;

    @Test
    public void getMetricsTest() {
        // Create short url
        ShortURL shortURL = createShortURL("http://example.com");

        // Fetch metrics via API
        Metrics metrics = getMetrics(shortURL);
        performMetricsAssertions(metrics, 0, 0, 0, 0);

        // Access shortened url with different parameters
        getShortUrl(shortURL, OperatingSystem.LINUX, Browser.FIREFOX);
        getShortUrl(shortURL, OperatingSystem.LINUX, Browser.CHROME);
        getShortUrl(shortURL, OperatingSystem.LINUX, Browser.OPERA);
        getShortUrl(shortURL, OperatingSystem.WINDOWS, Browser.FIREFOX);
        getShortUrl(shortURL, OperatingSystem.WINDOWS, Browser.FIREFOX);

        // Fetch metrics via API
        metrics = getMetrics(shortURL);
        performMetricsAssertions(metrics, 5, 1, 3, 2);
    }

    @Test
    @Ignore
    public void contentNegotiationTest() {
        // Create short url
        ShortURL shortURL = createShortURL("http://example.com");

        // Test content negotiation
        // Accept: text/html
        final String METRICS_URL = "http://localhost:" + port + "/api/metrics/" + shortURL.getHash();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Lists.asList(MediaType.TEXT_HTML, new MediaType[0]));
        ResponseEntity<Metrics> response = template.exchange(METRICS_URL, HttpMethod.GET, new HttpEntity(headers), Metrics.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
        assertThat(response.getHeaders().getLocation().toString(), is("/metrics/" + shortURL.getHash()));
        // Accept: application/json
        headers.setAccept(Lists.asList(MediaType.APPLICATION_JSON, new MediaType[0]));
        response = template.exchange(METRICS_URL, HttpMethod.GET, new HttpEntity(headers), Metrics.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        performMetricsAssertions(response.getBody(), 0, 0, 0, 0);
    }

    private ShortURL createShortURL(String url) {
        final String SHORTENER_URL = "http://localhost:" + port + "/link";
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("url", url);
        ResponseEntity<ShortURL> response = template.postForEntity(SHORTENER_URL, parts, ShortURL.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getBody();
    }

    private Metrics getMetrics(ShortURL shortURL) {
        final String METRICS_URL = "http://localhost:" + port + "/api/metrics/" + shortURL.getHash();
        ResponseEntity<Metrics> response = template.getForEntity(METRICS_URL, Metrics.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        return response.getBody();
    }

    private void getShortUrl(ShortURL shortURL, OperatingSystem operatingSystem, Browser browser) {
        final String SHORT_URL = "http://localhost:" + port + "/" + shortURL.getHash();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", new UserAgent(operatingSystem, browser).toString());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<Void> response = template.exchange(SHORT_URL, HttpMethod.GET, entity, Void.class);
        assertThat(response.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));
    }

    private void performMetricsAssertions(Metrics metrics, long numClicks, long uniqueVisitors, int clicksByBrowser, int clicksByOS) {
        assertThat(metrics.getClicks(), is(numClicks));
        assertThat(metrics.getUniqueVisitors(), is(uniqueVisitors));
        assertThat(metrics.getClicksByBrowser().size(), is(clicksByBrowser));
        assertThat(metrics.getClicksByOS().size(), is(clicksByOS));
    }

}
