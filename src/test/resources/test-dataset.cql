CREATE KEYSPACE IF NOT EXISTS fun WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};
