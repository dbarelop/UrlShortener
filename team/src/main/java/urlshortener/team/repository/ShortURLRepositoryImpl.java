package urlshortener.team.repository;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import urlshortener.team.domain.ShortURL;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {

	private static final Logger log = LoggerFactory
			.getLogger(ShortURLRepositoryImpl.class);

	private static final RowMapper<ShortURL> rowMapper = (rs, rowNum) -> {
        ShortURL rowMapper = new ShortURL(rs.getString("hash"), rs.getString("target"),
                null, rs.getString("sponsor"), rs.getDate("created"),
                rs.getString("owner"), rs.getBoolean("safe"), rs.getString("ip"),
                rs.getString("country"), rs.getString("user"));
        rowMapper.setLastStatus(rs.getInt("laststatus") == 0 ? null : HttpStatus.valueOf(rs.getInt("laststatus")));
        rowMapper.setLastCheck(rs.getDate("lastcheck"));
        return rowMapper;
    };

	@Autowired
	protected JdbcTemplate jdbc;

	public ShortURLRepositoryImpl() {
	}

	public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public ShortURL findByKey(String id) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash = ?", rowMapper, id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			log.error("When select for key " + id, e);
			return null;
		}
	}

	@Override
	public ShortURL save(ShortURL su) {
		try {
			jdbc.update("INSERT INTO shorturl VALUES (?,?,?,?,?,?,?,?,?,?,?)",
					su.getHash(), su.getTarget(), su.getSponsor(),
					su.getCreated(), su.getOwner(), su.getSafe(),
					su.getIP(), su.getCountry(), su.getLastStatus(), su.getLastCheck(),
					su.getUser());
		} catch (DuplicateKeyException e) {
			log.error("When insert for key " + su.getHash(), e);
			return su;
		} catch (Exception e) {
			log.error("When insert", e);
			return null;
		}
		return su;
	}

	@Override
	public ShortURL mark(ShortURL su, boolean safeness) {
		try {
			jdbc.update("UPDATE shorturl SET safe=? WHERE hash=?", safeness, su.getHash());
			ShortURL res = new ShortURL();
			BeanUtils.copyProperties(su, res);
			new DirectFieldAccessor(res).setPropertyValue("safe", safeness);
			return res;
		} catch (Exception e) {
			log.error("When update", e);
			return null;
		}
	}

	@Override
	public void update(ShortURL su) {
		try {
			jdbc.update(
					"update shorturl set target=?, sponsor=?, created=?, "
					+ "owner=?, safe=?, ip=?, country=?, laststatus=?,"
					+ "lastcheck=? where hash=?",
					su.getTarget(), su.getSponsor(), su.getCreated(),
					su.getOwner(), su.getSafe(), su.getIP(),
					su.getCountry(), su.getLastStatus(), su.getLastCheck(), su.getHash());
		} catch (Exception e) {
			log.error("When update for hash " + su.getHash(), e);
		}
	}

	@Override
	public void delete(String hash) {
		try {
			jdbc.update("delete from shorturl where hash=?", hash);
		} catch (Exception e) {
			log.error("When delete for hash " + hash, e);
		}
	}

	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from shorturl",
					Long.class);
		} catch (Exception e) {
			log.error("When counting", e);
		}
		return -1L;
	}

	@Override
	public List<ShortURL> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?",
					new Object[] { limit, offset }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			log.error("When select for limit " + limit + " and offset " + offset, e);
			return null;
		}
	}

	@Override
	public List<ShortURL> findAll() {
		try {
			return jdbc.query("SELECT * FROM shorturl", rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			log.error("When select all from shorturl", e);
			return null;
		}
	}

	@Override
	public List<ShortURL> findByTarget(String target) {
		try {
			return jdbc.query("SELECT * FROM shorturl WHERE target = ?", new Object[] { target }, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			log.error("When select for target " + target , e);
			return Collections.emptyList();
		}
	}
}
