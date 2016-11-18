package urlshortener.team.repository.fixture;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;

import java.sql.Date;

public class ClickFixture {

    public static Click click(ShortURL shortURL, Date created) {
        return new Click(null, shortURL.getHash(), created, null, null, null, null, null);
    }

    public static Click click(ShortURL shortURL, Date created, String ip) {
        return new Click(null, shortURL.getHash(), created, null, null, null, ip, null);
    }
}
