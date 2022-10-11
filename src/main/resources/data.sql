CREATE TABLE IF NOT EXISTS repository_language (
  id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  last_pull              DATE NOT NULL,
  repository_name                 VARCHAR      NOT NULL,
  language                 VARCHAR      NOT NULL,
  bytes              INT
);

CREATE TABLE IF NOT EXISTS last_update (
  id                     VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  last_pull              DATE NOT NULL,
  total_bytes              INT
);