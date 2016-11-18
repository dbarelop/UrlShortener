package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class ClickRepositoryImpl extends urlshortener.common.repository.ClickRepositoryImpl implements ClickRepository {

    private static final Logger log = LoggerFactory.getLogger(ClickRepositoryImpl.class);

    public ClickRepositoryImpl() {
    }

    public ClickRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long uniqueVisitorsByHash(String hash) {
        try {
            return jdbc.queryForObject("select count(distinct ip) from click where hash = ?", new Object[]{hash}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long uniqueVisitorsByHashBefore(String hash, Date endDate) {
        try {
            return jdbc.queryForObject("select count(distinct ip) from click where hash = ? and created <= ?", new Object[]{hash, endDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long uniqueVisitorsByHashAfter(String hash, Date startDate) {
        try {
            return jdbc.queryForObject("select count(distinct ip) from click where hash = ? and created >= ?", new Object[]{hash, startDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long uniqueVisitorsByHashBetween(String hash, Date startDate, Date endDate) {
        try {
            return jdbc.queryForObject("select count(distinct ip) from click where hash = ? and created >= ? and created <= ?", new Object[]{hash, startDate, endDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long clicksByHashBefore(String hash, Date endDate) {
        try {
            return jdbc.queryForObject("select count(*) from click where hash = ? and created <= ?", new Object[]{hash, endDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long clicksByHashAfter(String hash, Date startDate) {
        try {
            return jdbc.queryForObject("select count(*) from click where hash = ? and created >= ?", new Object[]{hash, startDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long clicksByHashBetween(String hash, Date startDate, Date endDate) {
        try {
            return jdbc.queryForObject("select count(*) from click where hash = ? and created >= ? and created <= ?", new Object[]{hash, startDate, endDate}, Long.class);
        } catch (Exception e) {
            log.debug("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }
}
