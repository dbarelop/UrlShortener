package urlshortener.team.repository;

import urlshortener.team.domain.VerificationRule;

import java.util.List;

public interface RuleRepository {
    VerificationRule save(VerificationRule rule);

    void update(VerificationRule rule);

    void delete(Long id);

    List<VerificationRule> findAll();

    List<VerificationRule> findByHash(String hash);
}
