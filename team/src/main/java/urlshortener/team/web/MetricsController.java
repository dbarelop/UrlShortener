package urlshortener.team.web;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.team.domain.Metrics;
import urlshortener.team.message.ErrorMessage;
import urlshortener.team.message.MetricsRequestMessage;
import urlshortener.team.repository.ClickRepository;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;
    @Autowired
    private ClickRepository clickRepository;

    @RequestMapping(value = "/metrics/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMetricsHtml(@PathVariable String hash,
                                 @RequestParam(value = "start_date", required = false) String startDateStr,
                                 @RequestParam(value = "end_date", required = false) String endDateStr,
                                 Model model, HttpServletResponse response) {
        logger.info("Requested HTML view for metrics with hash " + hash);
        /*ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Date startDate = startDateStr != null ? parseDate(startDateStr) : null;
        Date endDate = endDateStr != null ? parseDate(endDateStr) : null;
        Metrics metrics = getMetrics(shortURL, startDate, endDate);
        model.addAttribute("metrics", metrics);*/
        return "metrics";
    }

    @RequestMapping(value = "/api/metrics/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Void> redirect(@PathVariable String hash) throws URISyntaxException {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(new URI("/metrics/" + hash));
        return new ResponseEntity<>(h, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/metrics/{hash}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @MessageMapping("/metrics")
    @SendTo("/topic/metrics")
    public Object getMetricsWebSocket(MetricsRequestMessage message) {
        logger.info("Requested Websocket metrics for link with hash " + message.getHash());
        ShortURL shortURL = shortURLRepository.findByKey(message.getHash());
        if (shortURL == null) {
            return new ErrorMessage("URL with hash " + message.getHash() + " not found");
        }
        return getMetrics(shortURL, parseDate(message.getStartDate()), parseDate(message.getEndDate()));
    }

    private Metrics getMetrics(ShortURL shortURL, Date startDate, Date endDate) {
        // TODO: workaround
        URI uri = URI.create("http://localhost:8080/" + shortURL.getHash());
        //URI uri = linkTo(methodOn(UrlShortenerController.class).redirectTo(shortURL.getHash(), null)).toUri();
        Long clicks = clickRepository.clicksByHashBetween(shortURL.getHash(), startDate, endDate);
        Long uniqueVisitors = clickRepository.uniqueVisitorsByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<Browser, Long> clicksByBrowser = clickRepository.clicksForBrowserByHashBetween(shortURL.getHash(), startDate, endDate);
        Map<OperatingSystem, Long> clicksByOS = clickRepository.clicksForOSByHashBetween(shortURL.getHash(), startDate, endDate);
        Metrics metrics = new Metrics(uri, shortURL, clicks, uniqueVisitors, clicksByBrowser, clicksByOS);
        return metrics;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return SDF.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
