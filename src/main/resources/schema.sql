DROP TABLE IF EXISTS users, requests, items, bookings, comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL,
CONSTRAINT pk_user PRIMARY KEY (id),
CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
description VARCHAR(500),
requestor_id BIGINT NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT pk_request PRIMARY KEY (id),
CONSTRAINT fk_request FOREIGN KEY (requestor_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(500),
is_available BOOLEAN NOT NULL,
owner_id BIGINT NOT NULL,
request_id BIGINT,
CONSTRAINT pk_item PRIMARY KEY (item_id),
CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT fk_items_request FOREIGN KEY (request_id) REFERENCES requests(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
item_id BIGINT NOT NULL,
booker_id BIGINT NOT NULL,
status VARCHAR(25) NOT NULL,
CONSTRAINT pk_booking PRIMARY KEY (id),
CONSTRAINT fk_bookings_item FOREIGN KEY (item_id) REFERENCES items(item_id) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
text VARCHAR(500),
item_id BIGINT NOT NULL,
author_id BIGINT NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT pk_comment PRIMARY KEY (id),
CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items(item_id) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);