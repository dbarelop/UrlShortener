package urlshortener.team.service;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.domain.Metrics;

import java.util.Date;

public interface MetricsService {

    Metrics getMetrics(ShortURL shortURL, Date startDate, Date endDate);
}
