package urlshortener.team.repository;

import java.util.Date;

public interface ClickRepository extends urlshortener.common.repository.ClickRepository {

    Long clicksByHashBefore(String hash, Date endDate);

    Long clicksByHashAfter(String hash, Date startDate);

    Long clicksByHashBetween(String hash, Date startDate, Date endDate);

    Long uniqueVisitorsByHash(String hash);

    Long uniqueVisitorsByHashBefore(String hash, Date endDate);

    Long uniqueVisitorsByHashAfter(String hash, Date startDate);

    Long uniqueVisitorsByHashBetween(String hash, Date startDate, Date endDate);

    Long differentBrowsersByHash(String hash);

    Long differentBrowsersByHashBefore(String hash, Date endDate);

    Long differentBrowsersByHashAfter(String hash, Date startDate);

    Long differentBrowsersByHashBetween(String hash, Date startDate, Date endDate);

    Long differentOperatingSystemsByHash(String hash);

    Long differentOperatingSystemsByHashBefore(String hash, Date endDate);

    Long differentOperatingSystemsByHashAfter(String hash, Date startDate);

    Long differentOperatingSystemsByHashBetween(String hash, Date startDate, Date endDate);
}
