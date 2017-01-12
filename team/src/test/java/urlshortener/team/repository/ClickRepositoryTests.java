package urlshortener.team.repository;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.fixture.ClickFixture;
import urlshortener.team.repository.fixture.ShortURLFixture;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.repository.ShortURLRepositoryImpl;

import java.sql.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClickRepositoryTests {

    private EmbeddedDatabase db;
    private JdbcTemplate jdbc;
    private ShortURLRepository shortURLRepository;
    private ClickRepository clickRepository;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        shortURLRepository = new ShortURLRepositoryImpl(jdbc);
        clickRepository = new ClickRepositoryImpl(jdbc);
    }

    @After
    public void shutdown() {
        db.shutdown();
    }

    @Test
    public void thatReturnsClicksCorrectly() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click(shortURL, new Date(current)));
        clickRepository.save(ClickFixture.click(shortURL, new Date(before)));
        clickRepository.save(ClickFixture.click(shortURL, new Date(after)));

        assertEquals(clickRepository.clicksByHashBetween(shortURL.getHash(), null, null).longValue(), 3);
        assertEquals(clickRepository.clicksByHashBetween(shortURL.getHash(), null, new Date(current)).longValue(), 2);
        assertEquals(clickRepository.clicksByHashBetween(shortURL.getHash(), new Date(current), null).longValue(), 2);
        assertEquals(clickRepository.clicksByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }

    @Test
    public void thatReturnsUniqueVisitorsCorrectly() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click1(shortURL, new Date(current), "10.0.0.1"));
        clickRepository.save(ClickFixture.click1(shortURL, new Date(before), "10.0.0.2"));
        clickRepository.save(ClickFixture.click1(shortURL, new Date(after), "10.0.0.3"));

        assertEquals(clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), null, null).longValue(), 3);
        assertEquals(clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), null, new Date(current)).longValue(), 2);
        assertEquals(clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), new Date(current), null).longValue(), 2);
        assertEquals(clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }

    @Test
    public void thatReturnsClicksByBrowserCorrectly() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click2(shortURL, new Date(current), Browser.CHROME.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(current), Browser.CHROME.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(before), Browser.FIREFOX.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(before), Browser.FIREFOX.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(after), Browser.OPERA.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(after), Browser.OPERA.toString()));

        Map<Browser, Long> clicks = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), null, null);
        assertEquals(clicks.size(), 3);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 6);
        clicks = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), null, new Date(current));
        assertEquals(clicks.size(), 2);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 4);
        clicks = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), new Date(current), null);
        assertEquals(clicks.size(), 2);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 4);
        clicks = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), new Date(before), new Date(after));
        assertEquals(clicks.size(), 3);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 6);
    }

    @Test
    public void thatReturnsClicksByOSCorrectly() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click3(shortURL, new Date(current), OperatingSystem.LINUX.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(current), OperatingSystem.LINUX.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(before), OperatingSystem.MAC_OS.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(before), OperatingSystem.MAC_OS.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(after), OperatingSystem.WINDOWS.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(after), OperatingSystem.WINDOWS.toString()));

        Map<OperatingSystem, Long> clicks = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), null, null);
        assertEquals(clicks.size(), 3);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 6);
        clicks = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), null, new Date(current));
        assertEquals(clicks.size(), 2);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 4);
        clicks = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), new Date(current), null);
        assertEquals(clicks.size(), 2);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 4);
        clicks = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), new Date(before), new Date(after));
        assertEquals(clicks.size(), 3);
        assertEquals(clicks.values().stream().mapToLong(Long::longValue).sum(), 6);
    }
}
