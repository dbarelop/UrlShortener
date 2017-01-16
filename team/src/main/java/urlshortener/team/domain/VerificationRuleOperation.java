package urlshortener.team.domain;

public enum VerificationRuleOperation {
    CONTAINS,
    NOT_CONTAINS,
    TITLE_EQUALS;

    @Override
    public String toString() {
        switch (this) {
            case CONTAINS: return "Contains";
            case NOT_CONTAINS: return "Doesn't contain";
            case TITLE_EQUALS: return "Title equals";
        }
        return null;
    }
}
