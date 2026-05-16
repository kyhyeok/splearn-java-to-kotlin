-- Spring Data JDBC FK 컬럼 명명 규칙 적용
-- 규칙: 자식 테이블의 FK 컬럼명 = 부모 테이블명(= member)
-- 변경 전: member_detail.member_id
-- 변경 후: member_detail.member

ALTER TABLE member_detail RENAME COLUMN member_id TO member;
