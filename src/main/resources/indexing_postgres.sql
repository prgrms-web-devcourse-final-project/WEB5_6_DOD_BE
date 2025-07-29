-- 1. 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_event_members_event_member
    ON event_members(event_id, member_id);

-- CREATE INDEX IF NOT EXISTS idx_event_members_event_activated
--     ON event_members(event_id, confirmed);

-- 2. 인덱스 확인 (스키마 명시)
SELECT *
FROM pg_catalog.pg_indexes
WHERE tablename = 'event_members';

-- 3. 쿼리 성능 계획 확인
EXPLAIN
SELECT *
FROM event_members
WHERE event_id = 1
  AND member_id = 'GOOGLE_115434652372556552718'
LIMIT 1;

-- 실제 쿼리에 인덱스 사용되는지 확인. 지금 row 1개라 자동으로 seq scan(full scan)
EXPLAIN ANALYZE
SELECT *
FROM event_members
WHERE event_id = 1
  AND member_id = 'GOOGLE_115434652372556552718'
LIMIT 1;

-- 강제 인덱스 사용 유도
SET enable_seqscan = OFF;