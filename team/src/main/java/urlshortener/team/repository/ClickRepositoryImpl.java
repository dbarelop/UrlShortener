package urlshortener.team.repository;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ClickRepositoryImpl extends urlshortener.common.repository.ClickRepositoryImpl implements ClickRepository {

    private static final Logger log = LoggerFactory.getLogger(ClickRepositoryImpl.class);

    public ClickRepositoryImpl() {
    }

    public ClickRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long clicksByHashBetween(String hash, Date startDate, Date endDate) {
        List<Object> args = new ArrayList<>();
        args.add(hash);
        if (startDate != null) args.add(startDate);
        if (endDate != null) args.add(endDate);
        String query = "select count(*) from click where hash = ?";
        query += startDate != null ? " and created >= ?" : "";
        query += endDate != null ? " and created <= ?" : "";
        try {
            return jdbc.queryForObject(query, args.toArray(), Long.class);
        } catch (Exception e) {
            log.error("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Long uniqueVisitorsByHashBetween(String hash, Date startDate, Date endDate) {
        List<Object> args = new ArrayList<>();
        args.add(hash);
        if (startDate != null) args.add(startDate);
        if (endDate != null) args.add(endDate);
        String query = "select count(distinct ip) from click where hash = ?";
        query += startDate != null ? " and created >= ?" : "";
        query += endDate != null ? " and created <= ?" : "";
        try {
            return jdbc.queryForObject(query, args.toArray(), Long.class);
        } catch (Exception e) {
            log.error("When counting unique visitors for hash" + hash, e);
        }
        return -1L;
    }

    @Override
    public Map<Browser, Long> clicksForBrowserByHashBetween(String hash, Date startDate, Date endDate) {
        List<Object> args = new ArrayList<>();
        args.add(hash);
        if (startDate != null) args.add(startDate);
        if (endDate != null) args.add(endDate);
        String query = "select browser, count(*) as clicks from click where hash = ?";
        query += startDate != null ? " and created >= ?" : "";
        query += endDate != null ? " and created <= ?" : "";
        query += " group by browser";
        try {
            return jdbc.query(query, args.toArray(), rs -> {
                Map<Browser, Long> res = new HashMap<>();
                while (rs.next()) {
                    Browser browser = Browser.valueOf(rs.getString("browser"));
                    Long clicks = rs.getLong("clicks");
                    res.put(browser, clicks);
                }
                return res;
            });
        } catch (Exception e) {
            log.error("When counting unique visitors for hash" + hash, e);
        }
        return null;
    }

    @Override
    public Map<OperatingSystem, Long> clicksForOSByHashBetween(String hash, Date startDate, Date endDate) {
        List<Object> args = new ArrayList<>();
        args.add(hash);
        if (startDate != null) args.add(startDate);
        if (endDate != null) args.add(endDate);
        String query = "select platform as os, count(*) as clicks from click where hash = ?";
        query += startDate != null ? " and created >= ?" : "";
        query += endDate != null ? " and created <= ?" : "";
        query += " group by os";
        try {
            return jdbc.query(query, args.toArray(), rs -> {
                Map<OperatingSystem, Long> res = new HashMap<>();
                while (rs.next()) {
                    OperatingSystem os = OperatingSystem.valueOf(rs.getString("os"));
                    Long clicks = rs.getLong("clicks");
                    res.put(os, clicks);
                }
                return res;
            });
        } catch (Exception e) {
            log.error("When counting unique visitors for hash" + hash, e);
        }
        return null;
    }

    @Override
    public Date lastVisitDate(String ip) {
        String query = "select created from click where ip = ? order by created desc limit 1";
        try {
            return jdbc.queryForObject(query, new String[] { ip }, Date.class);
        } catch (Exception e) {
            log.error("When finding last visit of " + ip, e);
        }
        return null;
    }
}
