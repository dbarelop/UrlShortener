-- Clean database

DROP TABLE RULE IF EXISTS;
DROP TABLE CLICK IF EXISTS;
DROP TABLE SHORTURL IF EXISTS;

-- ShortURL

CREATE TABLE SHORTURL(
	HASH		  VARCHAR(30) PRIMARY KEY,	-- Key
	TARGET		VARCHAR(1024),						-- Original URL
	SPONSOR		VARCHAR(1024),						-- Sponsor URL
	CREATED 	TIMESTAMP,								-- Creation date
	OWNER		  VARCHAR(255),							-- User id
	SAFE		  BOOLEAN,									-- Safe target
	IP			  VARCHAR(20),							-- IP
	COUNTRY		VARCHAR(50),							-- Country
  LASTSTATUS INTEGER,                 -- Last check status
  LASTCHECKDATE	TIMESTAMP,            -- Last check date
  CACHEDATE	TIMESTAMP,                -- Cached version date
	USERNAME	VARCHAR(20),							-- User who created the short url
	VALID     BOOLEAN DEFAULT TRUE			-- Whether the last check passed the user rules
);

-- Click

CREATE TABLE CLICK(
	ID 				BIGINT IDENTITY,					-- KEY
	HASH 			VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL(HASH),	-- Foreing key
	CREATED 	TIMESTAMP,								-- Creation date
	REFERRER	VARCHAR(1024),						-- Traffic origin
	BROWSER		VARCHAR(50),							-- Browser
	PLATFORM	VARCHAR(50),							-- Platform
	IP				VARCHAR(20),							-- IP
	COUNTRY		VARCHAR(50)								-- Country
);

-- VerificationRule

CREATE TABLE RULE(
	ID				BIGINT IDENTITY,
	OPERATION	VARCHAR(30),
	TEXT			VARCHAR(255),
	HASH			VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL(HASH)
);
