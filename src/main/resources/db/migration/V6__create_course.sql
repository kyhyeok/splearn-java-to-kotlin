CREATE TABLE course (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    instructor_id BIGINT       NOT NULL,
    title         VARCHAR(100) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_COURSE_INSTRUCTOR_ID_TITLE UNIQUE (instructor_id, title),
    CONSTRAINT FK_COURSE_INSTRUCTOR_ID       FOREIGN KEY (instructor_id) REFERENCES instructor (id)
);

CREATE TABLE course_detail (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    description  VARCHAR(512) NULL,
    created_at   DATETIME(6)  NOT NULL,
    published_at DATETIME(6)  NULL,
    archived_at  DATETIME(6)  NULL,
    course       BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_COURSE_DETAIL_COURSE UNIQUE (course),
    CONSTRAINT FK_COURSE_DETAIL_COURSE FOREIGN KEY (course) REFERENCES course (id)
);
