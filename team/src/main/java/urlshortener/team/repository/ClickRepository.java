package urlshortener.team.repository;

public interface ClickRepository extends urlshortener.common.repository.ClickRepository {

    Long uniqueVisitorsByHash(String hash);
}
