package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.team.domain.Metrics;
import urlshortener.team.message.ErrorMessage;
import urlshortener.team.message.MetricsRequestMessage;
import urlshortener.team.service.MetricsServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ShortURLRepository shortURLRepository;
    @Autowired
    private MetricsServiceImpl service;

    @RequestMapping(value = "/metrics/{hash:(?!info).*}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMetricsHtml(@PathVariable String hash,
                                 @RequestParam(value = "start_date", required = false) String startDateStr,
                                 @RequestParam(value = "end_date", required = false) String endDateStr,
                                 Model model, HttpServletResponse response) {
        logger.info("Requested HTML view for metrics with hash " + hash);
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
        return service.getMetrics(shortURL, startDate, endDate);
    }

    @MessageMapping("/metrics/{hash}")
    @SendTo("/topic/metrics/{hash}")
    public Object getMetricsWebSocket(@DestinationVariable String hash, MetricsRequestMessage message) {
        logger.info("Requested Websocket metrics for link with hash " + hash);
        ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL == null) {
            return new ErrorMessage("URL with hash " + hash + " not found");
        }
        Date startDate = parseDate(message.getStartDate());
        Date endDate = parseDate(message.getEndDate());
        return service.getMetrics(shortURL, startDate, endDate);
    }

    void notifyNewMetrics(String hash) {
        logger.info("Notifying new metrics for hash " + hash);
        Metrics metrics = service.getMetrics(shortURLRepository.findByKey(hash), null, null);
        messagingTemplate.convertAndSend("/topic/metrics/" + hash, metrics);
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
