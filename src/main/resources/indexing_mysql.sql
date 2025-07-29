-- 1. 인덱스 생성
CREATE INDEX idx_event_members_event_member
    ON event_members(event_id, member_id);

CREATE INDEX idx_event_members_event_activated
    ON event_members(event_id, activated);

-- 2. 인덱스 확인 (MySQL에서는 SHOW INDEX 사용)
SHOW INDEX FROM event_members;

-- 3. 쿼리 성능 계획 확인
EXPLAIN
SELECT *
FROM event_members
WHERE event_id = 1
  AND member_id = 'GOOGLE_115434652372556552718'
    LIMIT 1;

-- mysql 은 전체 강제 인덱싱 사용 옵션이 따로 없음.

-- FORCE INDEX로 인덱스 유도 가능
EXPLAIN
SELECT *
FROM event_members FORCE INDEX (idx_event_members_event_member)
WHERE event_id = 1 AND member_id = 'GOOGLE_115434652372556552718'
    LIMIT 1;


SHOW INDEX FROM event_members;