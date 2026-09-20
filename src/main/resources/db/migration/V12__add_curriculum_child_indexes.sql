-- 자식 로딩·정렬 경로를 명시한다. InnoDB 의 FK 자동 인덱스에 기대면 FK 를 걷어내는 순간 인덱스도 사라진다
CREATE INDEX IX_SECTION_CURRICULUM_ORDER ON section (curriculum, section_order);
CREATE INDEX IX_LESSON_SECTION_ORDER ON lesson (section, lesson_order);
