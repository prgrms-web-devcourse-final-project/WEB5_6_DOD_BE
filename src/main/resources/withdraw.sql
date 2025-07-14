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
values ('1a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a@mail.com','이서준', 1, '010-1111-1111');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('2a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'x@mail.com','이강현', 1, '010-1111-1112');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('3a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'b@mail.com','안준희', 1, '010-1111-1113');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('4a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'c@mail.com','정서윤', 1, '010-1111-1114');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('5a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'd@mail.com','최동준', 1, '010-1111-1115');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('6a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'e@mail.com','박상윤', 1, '010-1111-1116');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('7a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'f@mail.com','박은서', 1, '010-1111-1117');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('8a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'g@mail.com','박준규', 1, '010-1111-1118');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('9a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'h@mail.com','현혜주', 1, '010-1111-1119');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('10a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'i@mail.com','황수지', 1, '010-1111-1121');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('11a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'j@mail.com','홍길동', 1, '010-1111-1131');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('12a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'k@mail.com','길동이', 1, '010-1111-1141');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('13a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'z@mail.com','해리포터', 1, '010-1111-5111');

-- schedule_member 테이블 생성
insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (1,'1a',1, 'ROLE_MASTER','서울역', 321.1234, 126.972836, '이서준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (2,'2a',1, 'ROLE_MEMBER','건대입구역', 37.540882, 127.071103, '이강현');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (3,'3a',1, 'ROLE_MEMBER','강남역', 37.497958, 127.027539, '안준희');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (4,'4a',1, 'ROLE_MEMBER','홍대입구역', 37.556748, 126.923643, '정서윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (5,'5a',1, 'ROLE_MEMBER','잠실역', 37.514649, 127.104267, '최동준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (6,'6a',1, 'ROLE_MEMBER','사당역', 37.476536, 126.981631, '박상윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (7,'7a',1, 'ROLE_MEMBER','을지로입구역', 37.565998, 126.982569, '박은서');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (8,'8a',1, 'ROLE_MEMBER','안국역', 37.576562, 126.98547, '박준규');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (9,'9a',1, 'ROLE_MEMBER','역삼역', 37.500658, 127.03643, '현혜주');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (10,'10a',1, 'ROLE_MEMBER','이수역', 37.487521, 126.982309, '황수지');

-- 그룹 멤버 삽입 (최동준: super leader)
insert into group_members(id, role, member_id, group_id, group_admin)
values (10000, 'GROUP_LEADER', '5a', 1, true);

-- 나머지 GROUP_LEADER 4명 (groupAdmin: false)
insert into group_members(id, role, member_id, group_id, group_admin)
values (10001, 'GROUP_LEADER', '1a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10002, 'GROUP_LEADER', '2a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10003, 'GROUP_LEADER', '3a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10004, 'GROUP_LEADER', '4a', 1, false);

-- 나머지 8명은 GROUP_MEMBER
insert into group_members(id, role, member_id, group_id, group_admin)
values (10005, 'GROUP_MEMBER', '6a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10006, 'GROUP_MEMBER', '7a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10007, 'GROUP_MEMBER', '8a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10008, 'GROUP_MEMBER', '9a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10009, 'GROUP_MEMBER', '10a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10010, 'GROUP_MEMBER', '11a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10011, 'GROUP_MEMBER', '12a', 1, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10012, 'GROUP_MEMBER', '13a', 1, false);
