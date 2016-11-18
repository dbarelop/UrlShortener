package urlshortener.team.repository;

import java.util.Date;

public interface ClickRepository extends urlshortener.common.repository.ClickRepository {

    Long uniqueVisitorsByHash(String hash);

    Long uniqueVisitorsByHashBefore(String hash, Date endDate);

    Long uniqueVisitorsByHashAfter(String hash, Date startDate);

    Long uniqueVisitorsByHashBetween(String hash, Date startDate, Date endDate);

    Long clicksByHashBefore(String hash, Date endDate);

    Long clicksByHashAfter(String hash, Date startDate);

    Long clicksByHashBetween(String hash, Date startDate, Date endDate);
}
