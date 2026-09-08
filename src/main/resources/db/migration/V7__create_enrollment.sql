CREATE TABLE enrollment (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    member_id    BIGINT      NOT NULL,
    course_id    BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL,
    enrolled_at  DATETIME(6) NOT NULL,
    completed_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_ENROLLMENT_MEMBER_ID_COURSE_ID UNIQUE (member_id, course_id),
    CONSTRAINT FK_ENROLLMENT_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_ENROLLMENT_COURSE_ID FOREIGN KEY (course_id) REFERENCES course (id)
);
