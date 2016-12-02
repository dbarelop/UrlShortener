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
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.repository.ShortURLRepositoryImpl;
import urlshortener.team.repository.fixture.ShortURLFixture;
import urlshortener.team.repository.fixture.ClickFixture;

import java.sql.Date;

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
    public void thatFiltersClicksByDate() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click(shortURL, new Date(current)));
        clickRepository.save(ClickFixture.click(shortURL, new Date(before)));
        clickRepository.save(ClickFixture.click(shortURL, new Date(after)));

        assertEquals(clickRepository.clicksByHash(shortURL.getHash()).longValue(), 3);
        assertEquals(clickRepository.clicksByHashAfter(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.clicksByHashBefore(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.clicksByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }

    @Test
    public void thatFiltersUniqueVisitorsByDate() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click1(shortURL, new Date(current), "10.0.0.1"));
        clickRepository.save(ClickFixture.click1(shortURL, new Date(before), "10.0.0.2"));
        clickRepository.save(ClickFixture.click1(shortURL, new Date(after), "10.0.0.3"));

        assertEquals(clickRepository.uniqueVisitorsByHash(shortURL.getHash()).longValue(), 3);
        assertEquals(clickRepository.uniqueVisitorsByHashAfter(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.uniqueVisitorsByHashBefore(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }

    @Test
    public void thatFiltersDifferentBrowsersByDate() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click2(shortURL, new Date(current), Browser.CHROME.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(before), Browser.FIREFOX.toString()));
        clickRepository.save(ClickFixture.click2(shortURL, new Date(after), Browser.OPERA.toString()));

        assertEquals(clickRepository.differentBrowsersByHash(shortURL.getHash()).longValue(), 3);
        assertEquals(clickRepository.differentBrowsersByHashAfter(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.differentBrowsersByHashBefore(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.differentBrowsersByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }

    @Test
    public void thatFiltersDifferentOperatingSystemsByDate() {
        ShortURL shortURL = ShortURLFixture.url1();
        long current = System.currentTimeMillis();
        long before = current - 10000;
        long after = current + 10000;

        shortURLRepository.save(ShortURLFixture.url1());
        clickRepository.save(ClickFixture.click3(shortURL, new Date(current), OperatingSystem.LINUX.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(before), OperatingSystem.MAC_OS.toString()));
        clickRepository.save(ClickFixture.click3(shortURL, new Date(after), OperatingSystem.WINDOWS.toString()));

        assertEquals(clickRepository.differentOperatingSystemsByHash(shortURL.getHash()).longValue(), 3);
        assertEquals(clickRepository.differentOperatingSystemsByHashAfter(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.differentOperatingSystemsByHashBefore(shortURL.getHash(), new Date(current)).longValue(), 2);
        assertEquals(clickRepository.differentOperatingSystemsByHashBetween(shortURL.getHash(), new Date(before), new Date(after)).longValue(), 3);
    }
}
