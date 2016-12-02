package urlshortener.team.web;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.team.domain.Metrics;
import urlshortener.team.repository.ClickRepository;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;
    @Autowired
    private ClickRepository clickRepository;

    @RequestMapping(value = "/metrics/{hash}", method = RequestMethod.GET)
    public String getMetrics(@PathVariable String hash,
                             @RequestParam(value = "start_date", required = false) String startDateStr,
                             @RequestParam(value = "end_date", required = false) String endDateStr,
                             Model model, HttpServletResponse response) {
        logger.info("Requested metrics for link with hash " + hash);
        ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Date startDate = startDateStr != null ? parseDate(startDateStr) : null;
        Date endDate = endDateStr != null ? parseDate(endDateStr) : null;
        Metrics metrics = getMetrics(shortURL, startDate, endDate);
        model.addAttribute("metrics", metrics);
        return "metrics";
    }

    @RequestMapping(value = "/metrics/{hash}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Metrics getMetricsJson(@PathVariable String hash,
                                                @RequestParam(value = "start_date", required = false) String startDateStr,
                                                @RequestParam(value = "end_date", required = false) String endDateStr,
                                                HttpServletResponse response) {
        logger.info("Requested JSON metrics for link with hash " + hash);
        ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Date startDate = startDateStr != null ? parseDate(startDateStr) : null;
        Date endDate = endDateStr != null ? parseDate(endDateStr) : null;
        return getMetrics(shortURL, startDate, endDate);
    }

    private Metrics getMetrics(ShortURL shortURL, Date startDate, Date endDate) {
        URI uri = linkTo(methodOn(UrlShortenerController.class).redirectTo(shortURL.getHash(), null)).toUri();
        Long clicks = clickRepository.clicksByHashBetween(shortURL.getHash(), startDate, endDate);
        Long uniqueVisitors = clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<Browser, Long> clicksByBrowser = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<OperatingSystem, Long> clicksByOS = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), startDate, endDate);
        Metrics metrics = new Metrics(uri, shortURL, clicks, uniqueVisitors, clicksByBrowser, clicksByOS);
        return metrics;
    }

    private Date parseDate(String dateStr) {
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return SDF.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
