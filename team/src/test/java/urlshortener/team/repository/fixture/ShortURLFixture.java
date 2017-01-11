package urlshortener.team.repository.fixture;

import urlshortener.team.domain.ShortURL;

public class ShortURLFixture {

    public static ShortURL url1() {
        return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, false, null, null);
    }
}
