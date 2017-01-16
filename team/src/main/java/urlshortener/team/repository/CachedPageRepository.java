package urlshortener.team.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import urlshortener.team.domain.CachedPage;

public interface CachedPageRepository extends MongoRepository<CachedPage, String> {
    CachedPage findById(String hash);
    boolean exists(String hash);
}
