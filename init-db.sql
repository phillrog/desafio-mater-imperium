SELECT 'CREATE DATABASE mater_imperium'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mater_imperium')\gexec

-- SELECT 'CREATE DATABASE mater_imperium_test'
-- WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mater_imperium_test')\gexec