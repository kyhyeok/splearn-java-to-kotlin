-- profile_address 컬럼 크기를 도메인 제약(15자)에 맞게 조정
-- 변경 전: VARCHAR(20)
-- 변경 후: VARCHAR(15)
ALTER TABLE member_detail MODIFY COLUMN profile_address VARCHAR(15) NULL;
