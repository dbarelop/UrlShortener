package urlshortener.team.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationRule {

    private Long id;
    private VerificationRuleOperation operation;
    private String text;
    private String hash;

    public VerificationRule() {
    }

    public VerificationRule(Long id, VerificationRuleOperation operation, String text, String hash) {
        this.id = id;
        this.operation = operation;
        this.text = text;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificationRuleOperation getOperation() {
        return operation;
    }

    public void setOperation(VerificationRuleOperation operation) {
        this.operation = operation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean validate(String body) {
        switch (operation) {
            case CONTAINS: return body.contains(text);
            case NOT_CONTAINS: return !body.contains(text);
            case TITLE_EQUALS: return body.contains("<title>" + text + "</title>");
        }
        return false;
    }
}
