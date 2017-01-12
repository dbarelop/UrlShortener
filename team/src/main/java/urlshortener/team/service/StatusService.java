package urlshortener.team.service;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.domain.CachedPage;

public interface StatusService {

    void periodicallyVerifyStatus();
    CachedPage getCachedPage(String hash);
    void verifyStatus(ShortURL shortURL);
}
