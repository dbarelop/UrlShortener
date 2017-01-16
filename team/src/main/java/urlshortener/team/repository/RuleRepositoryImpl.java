package urlshortener.team.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener.team.domain.VerificationRule;
import urlshortener.team.domain.VerificationRuleOperation;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Repository
public class RuleRepositoryImpl implements RuleRepository {

    private static final Logger logger = LoggerFactory.getLogger(RuleRepositoryImpl.class);

    private static final RowMapper<VerificationRule> rowMapper = (rs, rowNum) -> new VerificationRule(
            rs.getLong("id"),
            VerificationRuleOperation.valueOf(rs.getString("operation")),
            rs.getString("text"),
            rs.getString("hash"));

    @Autowired
    private JdbcTemplate jdbc;

    public RuleRepositoryImpl() {
    }

    public RuleRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public VerificationRule save(final VerificationRule rule) {
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO rule VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setNull(1, Types.BIGINT);
                ps.setString(2, rule.getOperation().name());
                ps.setString(3, rule.getText());
                ps.setString(4, rule.getHash());
                return ps;
            }, holder);
            new DirectFieldAccessor(rule).setPropertyValue("id", holder.getKey().longValue());
        } catch (DuplicateKeyException e) {
            logger.warn("When insert for rule with id " + rule.getId(), e);
            return rule;
        } catch (Exception e) {
            logger.error("When insert a rule", e);
            return null;
        }
        return rule;
    }

    @Override
    public void update(VerificationRule rule) {
        try {
            jdbc.update("UPDATE rule SET operation = ?, text = ? WHERE id = ?", rule.getOperation().name(), rule.getText(), rule.getId());
        } catch (Exception e) {
            logger.error("When update for rule with id " + rule.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            jdbc.update("DELETE FROM rule WHERE id = ?", id);
        } catch (Exception e) {
            logger.error("When delete for rule with id " + id, e);
        }
    }

    @Override
    public List<VerificationRule> findAll() {
        try {
            return jdbc.query("SELECT * FROM rule", rowMapper);
        } catch (Exception e) {
            logger.error("When select all rules", e);
            return null;
        }
    }

    @Override
    public List<VerificationRule> findByHash(String hash) {
        try {
            return jdbc.query("SELECT * FROM rule WHERE hash = ?", new Object[] { hash }, rowMapper);
        } catch (Exception e) {
            logger.error("When select all rules", e);
            return null;
        }
    }

}
