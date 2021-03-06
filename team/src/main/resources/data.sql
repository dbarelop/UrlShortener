
INSERT INTO SHORTURL (HASH, TARGET, SPONSOR, CREATED, OWNER, SAFE, IP, COUNTRY, LASTSTATUS, LASTCHECKDATE, CACHEDATE, USERNAME) VALUES
  ('hash0', 'https://moodle2.unizar.es/add/', NULL, '2017-01-02 03:04:05', NULL, NULL, '155.210.233.183', NULL, 200, NULL, NULL, NULL),
  ('hash1', 'http://www.w3schools.com/bootstrap/bootstrap_list_groups.asp', NULL, '2017-01-02 03:04:05', NULL, NULL, '155.210.233.183', NULL, 200, NULL, NULL, 'user'),
  ('hash2', 'http://stackoverflow.com/questions/13744779/exporting-intellij-idea-ui-form-to-eclipse', NULL, '2017-01-02 03:04:05', NULL, NULL, '155.210.233.183', NULL, 200, NULL, NULL, 'user'),
  ('hash3', 'http://stackoverflow.com/questions/24185378/intellij-idea-13-ui-designer-and-automatic-gradle-building', NULL, '2017-01-02 03:04:05', NULL, NULL, '155.210.233.183', NULL, 200, NULL, NULL, 'user'),
  ('hash4', 'http://stackoverflow.com/questions/12775170/how-do-i-create-a-new-swing-app-in-intellij-idea-community-edition', NULL, '2017-01-02 03:04:05', NULL, NULL, '155.210.233.183', NULL, 200, NULL, NULL, 'user');

INSERT INTO CLICK (HASH, CREATED, REFERRER, BROWSER, PLATFORM, IP, COUNTRY) VALUES
  ('hash0', '2017-01-08 04:05:06', NULL, 'FIREFOX', 'LINUX', '155.210.233.183', NULL),
  ('hash0', '2017-01-07 08:09:10', NULL, 'SAFARI', 'MAC_OS_X', '156.210.233.183', NULL),
  ('hash0', '2017-01-07 08:10:11', NULL, 'FIREFOX', 'MAC_OS_X', '157.210.233.183', NULL),
  ('hash0', '2017-01-07 05:06:07', NULL, 'EDGE', 'WINDOWS', '155.210.233.183', NULL),
  ('hash0', '2017-01-06 10:11:12', NULL, 'FIREFOX', 'MAC_OS_X', '158.210.233.183', NULL),
  ('hash0', '2017-01-06 06:07:08', NULL, 'CHROME', 'WINDOWS', '155.210.233.183', NULL),
  ('hash0', '2017-01-05 07:08:09', NULL, 'OPERA', 'MAC_OS_X', '155.210.233.183', NULL),
  ('hash0', '2017-01-05 08:09:10', NULL, 'SAFARI', 'IOS', '159.210.233.183', NULL),
  ('hash0', '2017-01-04 08:10:11', NULL, 'SAFARI', 'IOS', '160.210.233.183', NULL),
  ('hash0', '2017-01-03 08:09:10', NULL, 'CHROME', 'ANDROID', '161.210.233.183', NULL),
  ('hash0', '2017-01-02 08:10:11', NULL, 'CHROME', 'ANDROID', '162.210.233.183', NULL);

INSERT INTO RULE (OPERATION, TEXT, HASH) VALUES
  ('NOT_CONTAINS', 'bootstrap', 'hash1');
