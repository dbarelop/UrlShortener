package urlshortener.team.repository.fixture;

import urlshortener.team.domain.VerificationRule;
import urlshortener.team.domain.VerificationRuleOperation;

public class VerificationRuleFixture {

	public static VerificationRule rule1() {
        return new VerificationRule();
    }
	
    public static VerificationRule rule2() {
        return new VerificationRule(Long.valueOf(2), VerificationRuleOperation.CONTAINS, "bootstrap", "2");
    }
}
