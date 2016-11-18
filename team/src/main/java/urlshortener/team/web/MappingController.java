package urlshortener.team.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MappingController {

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET, produces = "text/http")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET, produces = "text/http")
    public String metrics() {
        return "metrics";
    }
}
