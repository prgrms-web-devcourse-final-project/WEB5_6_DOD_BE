INSERT INTO Members (id, password, provider, role, email, name, profile_image_number, tel)
VALUES ('GOOGLE_1234', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'test@gmail.com', '하명도', 1010101, '010-1234-5678');

/**
  강현 init
 */

-- 그룹 테이블 생성
insert into groups(id, name, description, is_grouped, activated)
values (1,'DOD 그룹','DOD 그룹입니당',true, true);

-- 이벤트 테이블 생성
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (1,'DOD 이벤트 생성', 10, 'OFFLINE', 'DOD 이벤트', 1, true);

-- 일정 테이블 생성
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (1, '2025-08-09 17:30:00','2025-08-09 23:00:00' , '강남역', 'NONE', '강남역 롯데시네마', 'FIXED',1,true, 'DOD 만나는 날입니당', '모여라! DOD!');

-- 멤버 테이블 생성
insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (1, 'google', 'ddd', 'ROLE_USER', 'a@mail.com','이서준', 1, '010-1111-1111');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (2, 'google', 'ddd', 'ROLE_USER', 'x@mail.com','이강현', 1, '010-1111-1112');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (3, 'google', 'ddd', 'ROLE_USER', 'b@mail.com','안준희', 1, '010-1111-1113');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (4, 'google', 'ddd', 'ROLE_USER', 'c@mail.com','정서윤', 1, '010-1111-1114');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (5, 'google', 'ddd', 'ROLE_USER', 'd@mail.com','최동준', 1, '010-1111-1115');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (6, 'google', 'ddd', 'ROLE_USER', 'e@mail.com','박상윤', 1, '010-1111-1116');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (7, 'google', 'ddd', 'ROLE_USER', 'f@mail.com','박은서', 1, '010-1111-1117');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (8, 'google', 'ddd', 'ROLE_USER', 'g@mail.com','박준규', 1, '010-1111-1118');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (9, 'google', 'ddd', 'ROLE_USER', 'h@mail.com','현혜주', 1, '010-1111-1119');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (10, 'google', 'ddd', 'ROLE_USER', 'i@mail.com','황수지', 1, '010-1111-1121');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (11, 'google', 'ddd', 'ROLE_USER', 'j@mail.com','홍길동', 1, '010-1111-1131');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (12, 'google', 'ddd', 'ROLE_USER', 'k@mail.com','길동이', 1, '010-1111-1141');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values (13, 'google', 'ddd', 'ROLE_USER', 'z@mail.com','해리포터', 1, '010-1111-5111');

-- schedule_member 테이블 생성
insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (1,1,1, 'ROLE_MASER','서울역', 321.1234, 126.972836, '이서준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (2,2,1, 'ROLE_MASER','건대입구역', 37.540882, 127.071103, '이강현');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (3,3,1, 'ROLE_MASER','강남역', 37.497958, 127.027539, '안준희');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (4,4,1, 'ROLE_MASER','홍대입구역', 37.556748, 126.923643, '정서윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (5,5,1, 'ROLE_MASER','잠실역', 37.514649, 127.104267, '최동준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (6,6,1, 'ROLE_MASER','사당역', 37.476536, 126.981631, '박상윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (7,7,1, 'ROLE_MASER','을지로입구역', 37.565998, 126.982569, '박은서');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (8,8,1, 'ROLE_MASER','안국역', 37.576562, 126.98547, '박준규');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (9,9,1, 'ROLE_MASER','역삼역', 37.500658, 127.03643, '현혜주');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (10,10,1, 'ROLE_MASER','이수역', 37.487521, 126.982309, '황수지');