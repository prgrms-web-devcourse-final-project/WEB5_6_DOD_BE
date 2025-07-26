
INSERT INTO Members (id, password, provider, role, email, name, profile_image_number, tel)
VALUES ('GOOGLE_1234', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'test@gmail.com', '하명도', 1, '010-1234-5678');


-- 그룹 1
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10001, '스터디 그룹', '백엔드 스터디 모임', true, NOW(), NOW());

-- 그룹 2
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10002, '헬스 친구', '운동 같이 하는 모임', true, NOW(), NOW());


-- 그룹 3 (isGrouped=false)
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10003, '비활성 모임', '아직 정식 그룹 아님', false, NOW(), NOW());

-- 그룹 5
INSERT INTO groups (id, name, description, is_grouped, created_at, modified_at)
VALUES (10005, '무슨모임인고임', '아아', true, NOW(), NOW());

-- 해당 그룹에 자걸이 추가
INSERT INTO group_members(id, activated, role, created_at, group_id, modified_at, member_id, group_admin)
VALUES (10004, true, 'GROUP_MEMBER', NOW(), 10003, NOW(), 'GOOGLE_115434652372556552718', false);


-- 자걸이를 그룹 1, 2에 추가
insert into group_members(id, activated, role, created_at, group_id, modified_at, member_id, group_admin)
values (10001, true, 'GROUP_MEMBER', '2025-07-15 00:47:03.635538', 10001, '2025-07-15 00:47:03.635538', 'GOOGLE_115434652372556552718', false);

insert into group_members(id, activated, role, created_at, group_id, modified_at, member_id, group_admin)
values (10002, true, 'GROUP_MEMBER', '2025-07-15 00:47:03.635538', 10002, '2025-07-15 00:47:03.635538', 'GOOGLE_115434652372556552718', false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10003, 'GROUP_LEADER', 'GOOGLE_1234', 10001, true);

insert into group_members(id, role, activated,created_at, group_id, modified_at, member_id)
values (10005, 'GROUP_MEMBER',true, '2025-07-15 00:47:03.635538', 10003, '2025-07-15 00:47:03.635538', 'GOOGLE_115434652372556552718');



-- 그룹 1 일정 이벤트
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (1,'DOD 이벤트 생성', 10, 'OFFLINE', 'DOD 이벤트', 10001, true);

-- 그룹 2 일정 이벤트
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (2,'DOD 이벤트 생성', 5, 'OFFLINE', 'DOD 이벤트', 10002, true);

-- 그룹 3 일정 이벤트 -> 일회성 일정 테스트
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (3,'합정 일회성 일정 테스트', 2, 'OFFLINE', '일회성 이벤트', 10003, true);


-- 오늘 일정
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (1, '2025-07-15 17:30:00','2025-07-15 23:00:00' , '강남역', 'NONE', '강남역 롯데시네마', 'FIXED',1,true, 'DOD 만나는 날입니당', '모여라! DOD!');

insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (2, '2025-08-01 17:30:00','2025-08-02 23:00:00' , '홍대입구역', 'NONE', '홍익대학교 정문 앞', 'FIXED',2,true, 'DOD 만나는 날인가?', '모일까말까');

-- 7/14~7/17 동안 진행되는 일정 (주간 걸치는지 확인용)
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (3, '2025-07-14 09:00:00','2025-07-17 18:00:00', '온라인', 'ZOOM', '링크 발송 예정', 'FIXED',1,true, '여러날 걸치는 일정', '멀티데이 일정 테스트');

insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (4, '2025-07-18 17:30:00','2025-07-18 23:00:00' , '합정역', 'NONE', '8번출구 앞', 'FIXED',3, true, '오늘이 날인가?', '모');


INSERT INTO schedule_members (id, schedule_id, member_id, activated)
VALUES
    (50001, 1, 'GOOGLE_115434652372556552718',true),
    (50002, 2, 'GOOGLE_115434652372556552718',true),
    (50003, 3, 'GOOGLE_115434652372556552718',true),
    (50004, 4, 'GOOGLE_115434652372556552718',true);

-- 이벤트 1 참가자 추가 (DOD 이벤트)
INSERT INTO event_members (id, role, confirmed, created_at, modified_at, member_id, event_id)
VALUES (20001, 'ROLE_MEMBER', false, NOW(), NOW(), 'GOOGLE_115434652372556552718', 1);

-- 이벤트 2 참가자 추가 (DOD 이벤트)
INSERT INTO event_members (id, role, confirmed, created_at, modified_at, member_id, event_id)
VALUES (20002, 'ROLE_MEMBER', false, NOW(), NOW(), 'GOOGLE_115434652372556552718', 2);

-- 이벤트 3 참가자 추가 (일회성 이벤트)
INSERT INTO event_members (id, role, confirmed, created_at, modified_at, member_id, event_id)
VALUES (20003, 'ROLE_MEMBER', false, NOW(), NOW(), 'GOOGLE_115434652372556552718', 3);

-- 이벤트 1 리더(생성자)
INSERT INTO event_members (id, role, confirmed, created_at, modified_at, member_id, event_id)
VALUES (20004, 'ROLE_MASTER', true, NOW(), NOW(), 'GOOGLE_1234', 1);