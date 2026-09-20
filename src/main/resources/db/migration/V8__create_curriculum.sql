CREATE TABLE curriculum (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_CURRICULUM_COURSE_ID UNIQUE (course_id),
    CONSTRAINT FK_CURRICULUM_COURSE_ID FOREIGN KEY (course_id) REFERENCES course (id)
);

CREATE TABLE section (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(200) NOT NULL,
    curriculum    BIGINT       NOT NULL,
    section_order INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_SECTION_CURRICULUM FOREIGN KEY (curriculum) REFERENCES curriculum (id)
);

CREATE TABLE lesson (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL,
    section      BIGINT       NOT NULL,
    lesson_order INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_LESSON_SECTION FOREIGN KEY (section) REFERENCES section (id)
);
