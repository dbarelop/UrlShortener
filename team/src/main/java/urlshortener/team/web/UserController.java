package urlshortener.team.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.VerificationRule;
import urlshortener.team.domain.VerificationRuleOperation;
import urlshortener.team.repository.RuleRepository;
import urlshortener.team.repository.ShortURLRepository;

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
// TODO: add security to AngularJS REST calls!
//@PreAuthorize("isAuthenticated()")
public class UserController {
    // TODO: error handling

    @Autowired
    private ShortURLRepository shortURLRepository;
    @Autowired
    private RuleRepository ruleRepository;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/userlinks", method = RequestMethod.GET)
    public String getUserLinks(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ShortURL> userLinks = shortURLRepository.findByUser(user.getUsername());
        userLinks.forEach(l -> l.setUri(linkTo(methodOn(RedirectionController.class).redirectTo(l.getHash(), null)).toUri()));
        model.addAttribute("user", user);
        model.addAttribute("userLinks", userLinks);
        model.addAttribute("ruleOperations", Arrays.asList(VerificationRuleOperation.values()));
        return "userLinks";
    }

    @RequestMapping(value = "/{hash}/rules", method = RequestMethod.GET)
    public ResponseEntity<List<VerificationRule>> getRules(@PathVariable("hash") String hash) {
        return new ResponseEntity<>(ruleRepository.findByHash(hash), HttpStatus.OK);
    }

    @RequestMapping(value = "/{hash}/rules", method = RequestMethod.POST)
    public ResponseEntity<VerificationRule> addRule(@PathVariable("hash") String hash, @RequestBody VerificationRule rule) {
        rule.setHash(hash);
        return new ResponseEntity<>(ruleRepository.save(rule), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{hash}/rules/{id}", method = RequestMethod.PUT)
    public void updateRule(@PathVariable("hash") String hash, @PathVariable("id") Long id, @RequestBody VerificationRule rule) {
        rule.setHash(hash);
        rule.setId(id);
        ruleRepository.update(rule);
    }

    @RequestMapping(value = "/{hash}/rules/{id}", method = RequestMethod.DELETE)
    public void deleteRule(@PathVariable("hash") String hash, @PathVariable("id") Long id) {
        ruleRepository.delete(id);
    }
}
