package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ClickRepositoryImpl extends urlshortener.common.repository.ClickRepositoryImpl implements ClickRepository {

    private static final Logger log = LoggerFactory.getLogger(ClickRepositoryImpl.class);

    @Override
    public Long uniqueVisitorsByHash(String hash) {
        try {
            return jdbc.queryForObject("select count(distinct ip) from click where hash = ?", new Object[]{hash}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }
}
