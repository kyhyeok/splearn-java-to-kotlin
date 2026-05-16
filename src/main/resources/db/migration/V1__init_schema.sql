CREATE TABLE member_detail (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    profile_address VARCHAR(20)  NULL,
    introduction    TEXT         NULL,
    registered_at   DATETIME(6)  NOT NULL,
    activated_at    DATETIME(6)  NULL,
    deactivated_at  DATETIME(6)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_MEMBER_DETAIL_PROFILE_ADDRESS UNIQUE (profile_address)
);

CREATE TABLE member (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email_address VARCHAR(150) NOT NULL,
    nickname      VARCHAR(100) NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    detail_id     BIGINT       NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_MEMBER_EMAIL_ADDRESS UNIQUE (email_address),
    CONSTRAINT UK_MEMBER_DETAIL_ID     UNIQUE (detail_id),
    CONSTRAINT FK_MEMBER_DETAIL_ID     FOREIGN KEY (detail_id) REFERENCES member_detail (id)
);
