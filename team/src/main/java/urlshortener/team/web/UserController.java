package urlshortener.team.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

import java.util.List;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private ShortURLRepository shortURLRepository;

    @RequestMapping("/userlinks")
    public String getUserLinks(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ShortURL> userLinks = shortURLRepository.findByUser(user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("userLinks", userLinks);
        return "userLinks";
    }
}
