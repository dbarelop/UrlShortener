package urlshortener.team.service;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.Metrics;
import urlshortener.team.repository.ClickRepository;

import java.util.Date;
import java.util.Map;

@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private ClickRepository clickRepository;

    public Metrics getMetrics(ShortURL shortURL, Date startDate, Date endDate) {
        Long clicks = clickRepository.clicksByHashBetween(shortURL.getHash(), startDate, endDate);
        Long uniqueVisitors = clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<Browser, Long> clicksByBrowser = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<OperatingSystem, Long> clicksByOS = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), startDate, endDate);
        Metrics metrics = new Metrics(shortURL.getHash(), shortURL, clicks, uniqueVisitors, clicksByBrowser, clicksByOS);
        return metrics;
    }
}
