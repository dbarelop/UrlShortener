package urlshortener.team.repository.fixture;

import urlshortener.team.domain.ShortURL;

public class ShortURLFixture {

    public static ShortURL url1() {
        return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, false, null, null, null);
    }
    
    public static ShortURL url2() {
        return new ShortURL("2", "http://www.unizar.es/", null, null, null, null, false, null, null, null);
    }
    
    public static ShortURL url3() {
        return new ShortURL("3", "http://www.unzar.es/", null, null, null, null, false, null, null, null);
    }
    
    public static ShortURL url4() {
        return new ShortURL("4", "https://www.facebook.com/", null, null, null, null, false, null, null, null);
    }
    
    public static ShortURL url5() {
        return new ShortURL("5", "https://www.youtube.com/", null, null, null, null, false, null, null, null);
    }
}
