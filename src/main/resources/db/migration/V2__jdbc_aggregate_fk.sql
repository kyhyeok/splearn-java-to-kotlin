-- Spring Data JDBC 집계 모델에 맞게 FK 방향 변경
-- 변경 전: member.detail_id → member_detail.id
-- 변경 후: member_detail.member_id → member.id

ALTER TABLE member_detail ADD COLUMN member_id BIGINT NULL;

UPDATE member_detail
SET member_id = (SELECT id FROM member WHERE member.detail_id = member_detail.id)
WHERE EXISTS (SELECT 1 FROM member WHERE member.detail_id = member_detail.id);

ALTER TABLE member_detail ADD CONSTRAINT UK_MEMBER_DETAIL_MEMBER_ID UNIQUE (member_id);
ALTER TABLE member_detail ADD CONSTRAINT FK_MEMBER_DETAIL_MEMBER_ID FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member DROP CONSTRAINT FK_MEMBER_DETAIL_ID;
ALTER TABLE member DROP CONSTRAINT UK_MEMBER_DETAIL_ID;
ALTER TABLE member DROP COLUMN detail_id;
