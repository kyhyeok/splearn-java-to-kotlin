-- 낙관적 락. curriculum(V9) 과 같은 패턴을 나머지 애그리거트 루트에 확장한다
ALTER TABLE member     ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE instructor ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE course     ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE enrollment ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
