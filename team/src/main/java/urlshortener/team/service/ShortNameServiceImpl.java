package urlshortener.team.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShortNameServiceImpl implements ShortNameService {
    // TODO: implement

    // Possible implementation: map of similar word lists
    private Map<String, List<String>> suggestions;
    // TODO: initialize
    private List<String> dict = new ArrayList<>();

    public boolean suggest(String word) {
        if (word.isEmpty()) {
            return false;
        }
        boolean suggestionAdded = false;
        for (String w : dict) {
            boolean coincidences = true;
            for (int i = 0; i < word.length(); i++) {
                if (word.length() <= w.length()) {
                    if (!word.toLowerCase().startsWith(String.valueOf(w.toLowerCase().charAt(i)), i)) {
                        coincidences = false;
                        break;
                    }
                }
            }
            if (coincidences) {
                if (word.length() < w.length()) {
                    //addSuggestions(word);
                    suggestionAdded = true;
                }
            }
        }
        return suggestionAdded;
    }

    private void addSuggestion() {
        // TODO: implement
    }
}
