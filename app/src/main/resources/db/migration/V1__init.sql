CREATE TABLE cities
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_cities PRIMARY KEY (id)
);

CREATE TABLE listings
(
    id               UUID             NOT NULL,
    title            VARCHAR(255)     NOT NULL,
    description      VARCHAR(255)     NOT NULL,
    property_type    VARCHAR(255)     NOT NULL,
    listing_type     VARCHAR(255)     NOT NULL,
    city_id          UUID,
    neighbourhood_id UUID,
    owner_id         UUID             NOT NULL,
    address          VARCHAR(255)     NOT NULL,
    area             FLOAT            NOT NULL,
    floor            INTEGER          NOT NULL,
    rooms            INTEGER          NOT NULL,
    pricing          DOUBLE PRECISION NOT NULL,
    listing_visits   INTEGER          NOT NULL,
    created_at       TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at       TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_listings PRIMARY KEY (id)
);

CREATE TABLE neighbourhoods
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    city_id    UUID         NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_neighbourhoods PRIMARY KEY (id)
);

CREATE TABLE profiles
(
    id            UUID         NOT NULL,
    auth0_id      VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    picture_url   VARCHAR(1024),
    display_name  VARCHAR(255),
    contact_email VARCHAR(255),
    phone         VARCHAR(255),
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_profiles PRIMARY KEY (id)
);

ALTER TABLE cities
    ADD CONSTRAINT uc_cities_name UNIQUE (name);

ALTER TABLE profiles
    ADD CONSTRAINT uc_profiles_auth0 UNIQUE (auth0_id);

ALTER TABLE neighbourhoods
    ADD CONSTRAINT uq_neighbourhoods_city_name UNIQUE (city_id, name);

ALTER TABLE listings
    ADD CONSTRAINT FK_LISTINGS_ON_CITY FOREIGN KEY (city_id) REFERENCES cities (id);

ALTER TABLE listings
    ADD CONSTRAINT FK_LISTINGS_ON_NEIGHBOURHOOD FOREIGN KEY (neighbourhood_id) REFERENCES neighbourhoods (id);

ALTER TABLE listings
    ADD CONSTRAINT FK_LISTINGS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES profiles (id);

ALTER TABLE neighbourhoods
    ADD CONSTRAINT FK_NEIGHBOURHOODS_ON_CITY FOREIGN KEY (city_id) REFERENCES cities (id);