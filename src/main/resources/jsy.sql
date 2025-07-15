-- 그룹 1
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10001, '스터디 그룹', '백엔드 스터디 모임', true, NOW(), NOW());

-- 그룹 2
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10002, '헬스 친구', '운동 같이 하는 모임', true, NOW(), NOW());

-- 자걸이를 그룹 1, 2에 추가
insert into group_members(id, activated, role, created_at, group_id, modified_at, member_id, group_admin)
values (10001, true, 'GROUP_MEMBER', '2025-07-15 00:47:03.635538', 10001, '2025-07-15 00:47:03.635538', 'GOOGLE_115434652372556552718', false);

insert into group_members(id, activated, role, created_at, group_id, modified_at, member_id, group_admin)
values (10002, true, 'GROUP_MEMBER', '2025-07-15 00:47:03.635538', 10002, '2025-07-15 00:47:03.635538', 'GOOGLE_115434652372556552718', false);

-- 그룹 1 일정 이벤트
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (1,'DOD 이벤트 생성', 10, 'OFFLINE', 'DOD 이벤트', 10001, true);

-- 그룹 2 일정 이벤트
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (2,'DOD 이벤트 생성', 5, 'OFFLINE', 'DOD 이벤트', 10002, true);

-- 오늘 일정
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (1, '2025-07-15 17:30:00','2025-07-15 23:00:00' , '강남역', 'NONE', '강남역 롯데시네마', 'FIXED',1,true, 'DOD 만나는 날입니당', '모여라! DOD!');

insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (2, '2025-08-01 17:30:00','2025-08-02 23:00:00' , '홍대입구역', 'NONE', '홍익대학교 정문 앞', 'FIXED',2,true, 'DOD 만나는 날인가?', '모일까말까');

-- 7/14~7/17 동안 진행되는 일정 (주간 걸치는지 확인용)
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (3, '2025-07-14 09:00:00','2025-07-17 18:00:00', '온라인', 'ZOOM', '링크 발송 예정', 'FIXED',1,true, '여러날 걸치는 일정', '멀티데이 일정 테스트');


INSERT INTO schedule_members (id, schedule_id, member_id)
VALUES
    (50001, 1, 'GOOGLE_115434652372556552718'),
    (50002, 2, 'GOOGLE_115434652372556552718');