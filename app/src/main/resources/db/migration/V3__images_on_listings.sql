CREATE TABLE listing_images
(
    listing_id UUID         NOT NULL,
    object_key VARCHAR(255) NOT NULL,
    position   INTEGER      NOT NULL,
    CONSTRAINT pk_listing_images PRIMARY KEY (listing_id, position)
);