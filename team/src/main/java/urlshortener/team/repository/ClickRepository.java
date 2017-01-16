package urlshortener.team.repository;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;

import java.util.Date;
import java.util.Map;

public interface ClickRepository extends urlshortener.common.repository.ClickRepository {

    Long clicksByHashBetween(String hash, Date startDate, Date endDate);

    Long uniqueVisitorsByHashBetween(String hash, Date startDate, Date endDate);

    Map<Browser, Long> clicksForBrowserByHashBetween(String hash, Date startDate, Date endDate);

    Map<OperatingSystem, Long> clicksForOSByHashBetween(String hash, Date startDate, Date endDate);

    Date lastVisitDate(String ip);
}
