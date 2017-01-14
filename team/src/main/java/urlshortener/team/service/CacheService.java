package urlshortener.team.service;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.CachedPage;

public interface CacheService {

    void periodicallyVerifyStatus();
    CachedPage getCachedPage(String hash);
    boolean isCached(String hash);
    void verifyStatus(ShortURL shortURL);
}
