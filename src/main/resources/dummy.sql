-- MEMBER
insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('GOOGLE_1111', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'seojun@gmail.com','이서준', 1, '010-1111-1111');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('GOOGLE_2222', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'kanghyeon@gmail.com','이강현', 2, '010-1111-1112');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('GOOGLE_3333', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'junhui@gmail.com','안준희', 3, '010-1111-1113');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('GOOGLE_4444', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'seoyoon@mail.com','정서윤', 4, '010-1111-1114');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('GOOGLE_5555', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'dongjun@gmail.com','최동준', 5, '010-1111-1115');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('KAKAO_1111', '{noop}123qwe!@#', 'KAKAO', 'ROLE_USER', 'sangyoon@kakao.com','박상윤', 6, '010-1111-1116');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('KAKAO_2222', '{noop}123qwe!@#', 'KAKAO', 'ROLE_USER', 'eunseo@kakao.com','박은서', 7, '010-1111-1117');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('KAKAO_3333', '{noop}123qwe!@#', 'KAKAO', 'ROLE_USER', 'jungyu@kakao.com','박준규', 8, '010-1111-1118');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('KAKAO_4444', '{noop}123qwe!@#', 'KAKAO', 'ROLE_USER', 'hyezu@kakao.com','현혜주', 9, '010-1111-1119');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('KAKAO_5555', '{noop}123qwe!@#', 'KAKAO', 'ROLE_USER', 'suji@kakao.com','황수지', 0, '010-1111-1121');

-- Group
insert into groups(id, name, description, is_grouped, activated)
values (10077,'그래도 해야지 어떡해','프로그래머스 데브코스 최종 프로젝트 7팀입니다.',true, true);

-- GroupMember
insert into group_members(id, role, member_id, group_id, group_admin)
values (10070, 'GROUP_LEADER', 'GOOGLE_1111', 10077, true);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10071, 'GROUP_LEADER', 'GOOGLE_2222', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10072, 'GROUP_MEMBER', 'GOOGLE_3333', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10073, 'GROUP_MEMBER', 'GOOGLE_4444', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10074, 'GROUP_MEMBER', 'GOOGLE_5555', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10075, 'GROUP_LEADER', 'KAKAO_1111', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10076, 'GROUP_MEMBER', 'KAKAO_2222', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10077, 'GROUP_MEMBER', 'KAKAO_3333', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10078, 'GROUP_MEMBER', 'KAKAO_4444', 10077, false);

insert into group_members(id, role, member_id, group_id, group_admin)
values (10079, 'GROUP_MEMBER', 'KAKAO_5555', 10077, false);

-- Event 1 : for Offline
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (70,'DOD 이번에 한번 모이자!', 10, 'OFFLINE', 'DOD 오프라인 모임을 위한 이벤트', 10077, true);

-- EventMember 1
insert into event_members(id, role, confirmed, member_id, event_id)
values (70, 'ROLE_MASTER', true, 'GOOGLE_1111', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (71, 'ROLE_MEMBER', true, 'GOOGLE_2222', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (72, 'ROLE_MEMBER', true, 'GOOGLE_3333', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (73, 'ROLE_MEMBER', true, 'GOOGLE_4444', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (74, 'ROLE_MEMBER', true, 'GOOGLE_5555', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (75, 'ROLE_MEMBER', true, 'KAKAO_1111', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (76, 'ROLE_MEMBER', true, 'KAKAO_2222', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (77, 'ROLE_MEMBER', true, 'KAKAO_3333', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (78, 'ROLE_MEMBER', true, 'KAKAO_4444', 70);

insert into event_members(id, role, confirmed, member_id, event_id)
values (79, 'ROLE_MEMBER', true, 'KAKAO_5555', 70);

-- Schedule 1
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (70, '2025-08-09 17:30:00','2025-08-09 23:00:00' , '강남', 'NONE', '강남역 롯데시네마', 'FIXED', 70, true, 'DOD 만나는 날입니당', '모여라! DOD!');
-- ScheduleMember 1
insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (70,'GOOGLE_1111',70, 'ROLE_MASTER','서울역', 37.55315, 126.972533, '이서준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (71,'GOOGLE_2222',70, 'ROLE_MEMBER','까치산', 37.531398, 126.846942, '이강현');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (72,'GOOGLE_3333',70, 'ROLE_MEMBER','신설동', 37.576117, 127.02471, '안준희');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (73,'GOOGLE_4444',70, 'ROLE_MEMBER','을지로3가', 37.566292, 126.991773, '정서윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (74,'GOOGLE_5555',70, 'ROLE_MEMBER','대곡', 37.631629, 126.811025, '최동준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (75,'KAKAO_1111',70, 'ROLE_MEMBER','홍대입구', 37.556748, 126.923643, '박상윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (76,'KAKAO_2222',70, 'ROLE_MEMBER','잠실', 37.513305, 127.100129, '박은서');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (77,'KAKAO_3333',70, 'ROLE_MEMBER','의정부', 37.73873, 127.045891, '박준규');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (78,'KAKAO_4444',70, 'ROLE_MEMBER','안국', 37.576562, 126.98547, '현혜주');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (79,'KAKAO_5555',70, 'ROLE_MEMBER','여의도', 37.521578, 126.924318, '황수지');

-- Workspace 1
insert into workspaces(id,schedule_id,type,name,url)
values (70,70,'DISCORD','DOD discord','www.discord.com');

insert into workspaces(id,schedule_id,type,name,url)
values (71,70,'FIGMA','이때 어때 figma','www.figma.com');

insert into workspaces(id,schedule_id,type,name,url)
values (72,70,'ZEP','데브코스 zep','www.zep.com');

-- Event 2 : for Online
insert into events(id, description, max_member, meeting_type, title, group_id, activated)
values (71,'최종 프로젝트 대비 회의합시다~!', 10, 'ONLINE', 'DOD 온라인 미팅을 위한 이벤트', 10077, true);

-- EventMember 2
insert into event_members(id, role, confirmed, member_id, event_id)
values (770, 'ROLE_MASTER', true, 'GOOGLE_1111', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (771, 'ROLE_MEMBER', true, 'GOOGLE_2222', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (772, 'ROLE_MEMBER', true, 'GOOGLE_3333', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (773, 'ROLE_MEMBER', true, 'GOOGLE_4444', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (774, 'ROLE_MEMBER', true, 'GOOGLE_5555', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (775, 'ROLE_MEMBER', true, 'KAKAO_1111', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (776, 'ROLE_MEMBER', true, 'KAKAO_2222', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (777, 'ROLE_MEMBER', true, 'KAKAO_3333', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (778, 'ROLE_MEMBER', true, 'KAKAO_4444', 71);

insert into event_members(id, role, confirmed, member_id, event_id)
values (779, 'ROLE_MEMBER', true, 'KAKAO_5555', 71);

-- Schedule 2
insert into schedules(id, start_time, end_time, location, meeting_platform, specific_location, status, event_id, activated, description, schedule_name)
values (71, '2025-08-04 09:00:00','2025-08-04 12:30:00' , null, 'GOOGLE_MEET', null, 'FIXED', 71, true, '최종 발표 PPT 준비 됐나요??', '이때어때 최종 발표 준비 회의');

-- ScheduleMember 2
insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (770,'GOOGLE_1111',71, 'ROLE_MEMBER',null, null, null, '이서준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (771,'GOOGLE_2222',71, 'ROLE_MEMBER',null, null, null, '이강현');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (772,'GOOGLE_3333',71, 'ROLE_MEMBER',null, null, null, '안준희');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (773,'GOOGLE_4444',71, 'ROLE_MEMBER',null, null, null, '정서윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (774,'GOOGLE_5555',71, 'ROLE_MEMBER',null, null, null, '최동준');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (775,'KAKAO_1111',71, 'ROLE_MEMBER',null, null, null, '박상윤');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (776,'KAKAO_2222',71, 'ROLE_MEMBER',null, null, null, '박은서');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (777,'KAKAO_3333',71, 'ROLE_MASTER',null, null, null, '박준규');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (778,'KAKAO_4444',71, 'ROLE_MEMBER',null, null, null, '현혜주');

insert into schedule_members(id, member_id, schedule_id, role, depart_location_name, latitude, longitude, name)
values (779,'KAKAO_5555',71, 'ROLE_MEMBER',null, null, null, '황수지');

-- Workspace 2
insert into workspaces(id,schedule_id,type,name,url)
values (73,71,'GOOGLE_DOS','Team07 구글 문서','www.docs.google.com');

insert into workspaces(id,schedule_id,type,name,url)
values (74,71,'MIRO','DOD Miro','www.miro.com');

insert into workspaces(id,schedule_id,type,name,url)
values (75,71,'CANVA','이떄 어때 Canva','www.canva.com');