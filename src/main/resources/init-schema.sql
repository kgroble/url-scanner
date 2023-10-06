CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE screenshots (
    id       uuid  NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    filename text  NOT NULL,
    content  bytea NOT NULL
);
