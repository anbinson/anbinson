CREATE KEYSPACE IF NOT EXISTS odsdb WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};
CREATE TABLE IF NOT EXISTS odsdb.kc03(trans_id text, name text, balance double, PRIMARY KEY (trans_id));
