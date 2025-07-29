-- 그룹 테이블 생성
insert into teams(id, name, description, is_grouped, activated)
values (1,'DOD 그룹','DOD 그룹입니당',true, true);



show engine innodb status;

-- 이벤트 테이블 생성
insert into events(id, description, max_member, meeting_type, title, team_id, activated, version)
values (4,'일정 생성 이벤트', 10, 'OFFLINE', 'DOD 이벤트', 1, true, 0);

-- 멤버 테이블 생성
insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('1a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a1@mail.com','참가자1', 1, '010-0000-0001');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('2a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a2@mail.com','참가자2', 1, '010-0000-0002');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('3a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a3@mail.com','참가자3', 1, '010-0000-0003');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('4a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a4@mail.com','참가자4', 1, '010-0000-0004');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('5a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a5@mail.com','참가자5', 1, '010-0000-0005');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('6a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a6@mail.com','참가자6', 1, '010-0000-0006');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('7a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a7@mail.com','참가자7', 1, '010-0000-0007');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('8a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a8@mail.com','참가자8', 1, '010-0000-0008');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('9a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a9@mail.com','참가자9', 1, '010-0000-0009');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('10a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a10@mail.com','참가자10', 1, '010-0000-0010');



-- 그룹 멤버 삽입 (참가자1: super leader)
insert into group_members(id, role, member_id, team_id, group_admin)
values (10001, 'GROUP_LEADER', '1a', 1, true);

-- 나머지 GROUP_LEADER 1명 (참가자2, groupAdmin: false)
insert into group_members(id, role, member_id, team_id, group_admin)
values (10002, 'GROUP_LEADER', '2a', 1, false);

-- 나머지 98명은 GROUP_MEMBER
insert into group_members(id, role, member_id, team_id, group_admin)
values (10003, 'GROUP_MEMBER', '3a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10004, 'GROUP_MEMBER', '4a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10005, 'GROUP_MEMBER', '5a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10006, 'GROUP_MEMBER', '6a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10007, 'GROUP_MEMBER', '7a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10008, 'GROUP_MEMBER', '8a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10009, 'GROUP_MEMBER', '9a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10010, 'GROUP_MEMBER', '10a', 1, false);
