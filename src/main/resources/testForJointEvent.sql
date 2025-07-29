-- 그룹 테이블 생성
insert into teams(id, name, description, is_grouped, activated)
values (1,'DOD 그룹','DOD 그룹입니당',true, true);

-- 이벤트 테이블 생성
insert into events(id, description, max_member, meeting_type, title, team_id, activated)
values (1,'DOD 이벤트 생성', 10, 'OFFLINE', 'DOD 이벤트', 1, true);

-- 이벤트 테이블 생성(예외용)
insert into events(id, description, max_member, meeting_type, title, team_id, activated)
values (2,'DOD 이벤트 생성 이미 있는 사람', 2, 'OFFLINE', '예외 테스트1', 1, true);
insert into events(id, description, max_member, meeting_type, title, team_id, activated)
values (3,'DOD 이벤트 생성', 1, 'OFFLINE', 'DOD 이벤트', 1, true);

-- 멤버 테이블 생성(예외용)
insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('Exception1', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'exception1@mail.com','예외참가자1', 1, '010-1111-1111');

insert into group_members(id, role, member_id, team_id, group_admin)
values (99999, 'GROUP_MEMBER', 'Exception1', 1, false);



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

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('11a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a11@mail.com','참가자11', 1, '010-0000-0011');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('12a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a12@mail.com','참가자12', 1, '010-0000-0012');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('13a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a13@mail.com','참가자13', 1, '010-0000-0013');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('14a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a14@mail.com','참가자14', 1, '010-0000-0014');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('15a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a15@mail.com','참가자15', 1, '010-0000-0015');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('16a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a16@mail.com','참가자16', 1, '010-0000-0016');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('17a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a17@mail.com','참가자17', 1, '010-0000-0017');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('18a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a18@mail.com','참가자18', 1, '010-0000-0018');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('19a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a19@mail.com','참가자19', 1, '010-0000-0019');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('20a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a20@mail.com','참가자20', 1, '010-0000-0020');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('21a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a21@mail.com','참가자21', 1, '010-0000-0021');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('22a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a22@mail.com','참가자22', 1, '010-0000-0022');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('23a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a23@mail.com','참가자23', 1, '010-0000-0023');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('24a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a24@mail.com','참가자24', 1, '010-0000-0024');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('25a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a25@mail.com','참가자25', 1, '010-0000-0025');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('26a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a26@mail.com','참가자26', 1, '010-0000-0026');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('27a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a27@mail.com','참가자27', 1, '010-0000-0027');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('28a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a28@mail.com','참가자28', 1, '010-0000-0028');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('29a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a29@mail.com','참가자29', 1, '010-0000-0029');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('30a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a30@mail.com','참가자30', 1, '010-0000-0030');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('31a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a31@mail.com','참가자31', 1, '010-0000-0031');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('32a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a32@mail.com','참가자32', 1, '010-0000-0032');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('33a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a33@mail.com','참가자33', 1, '010-0000-0033');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('34a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a34@mail.com','참가자34', 1, '010-0000-0034');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('35a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a35@mail.com','참가자35', 1, '010-0000-0035');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('36a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a36@mail.com','참가자36', 1, '010-0000-0036');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('37a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a37@mail.com','참가자37', 1, '010-0000-0037');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('38a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a38@mail.com','참가자38', 1, '010-0000-0038');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('39a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a39@mail.com','참가자39', 1, '010-0000-0039');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('40a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a40@mail.com','참가자40', 1, '010-0000-0040');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('41a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a41@mail.com','참가자41', 1, '010-0000-0041');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('42a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a42@mail.com','참가자42', 1, '010-0000-0042');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('43a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a43@mail.com','참가자43', 1, '010-0000-0043');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('44a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a44@mail.com','참가자44', 1, '010-0000-0044');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('45a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a45@mail.com','참가자45', 1, '010-0000-0045');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('46a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a46@mail.com','참가자46', 1, '010-0000-0046');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('47a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a47@mail.com','참가자47', 1, '010-0000-0047');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('48a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a48@mail.com','참가자48', 1, '010-0000-0048');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('49a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a49@mail.com','참가자49', 1, '010-0000-0049');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('50a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a50@mail.com','참가자50', 1, '010-0000-0050');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('51a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a51@mail.com','참가자51', 1, '010-0000-0051');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('52a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a52@mail.com','참가자52', 1, '010-0000-0052');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('53a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a53@mail.com','참가자53', 1, '010-0000-0053');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('54a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a54@mail.com','참가자54', 1, '010-0000-0054');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('55a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a55@mail.com','참가자55', 1, '010-0000-0055');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('56a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a56@mail.com','참가자56', 1, '010-0000-0056');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('57a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a57@mail.com','참가자57', 1, '010-0000-0057');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('58a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a58@mail.com','참가자58', 1, '010-0000-0058');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('59a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a59@mail.com','참가자59', 1, '010-0000-0059');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('60a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a60@mail.com','참가자60', 1, '010-0000-0060');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('61a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a61@mail.com','참가자61', 1, '010-0000-0061');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('62a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a62@mail.com','참가자62', 1, '010-0000-0062');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('63a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a63@mail.com','참가자63', 1, '010-0000-0063');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('64a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a64@mail.com','참가자64', 1, '010-0000-0064');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('65a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a65@mail.com','참가자65', 1, '010-0000-0065');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('66a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a66@mail.com','참가자66', 1, '010-0000-0066');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('67a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a67@mail.com','참가자67', 1, '010-0000-0067');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('68a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a68@mail.com','참가자68', 1, '010-0000-0068');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('69a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a69@mail.com','참가자69', 1, '010-0000-0069');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('70a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a70@mail.com','참가자70', 1, '010-0000-0070');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('71a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a71@mail.com','참가자71', 1, '010-0000-0071');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('72a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a72@mail.com','참가자72', 1, '010-0000-0072');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('73a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a73@mail.com','참가자73', 1, '010-0000-0073');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('74a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a74@mail.com','참가자74', 1, '010-0000-0074');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('75a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a75@mail.com','참가자75', 1, '010-0000-0075');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('76a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a76@mail.com','참가자76', 1, '010-0000-0076');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('77a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a77@mail.com','참가자77', 1, '010-0000-0077');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('78a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a78@mail.com','참가자78', 1, '010-0000-0078');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('79a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a79@mail.com','참가자79', 1, '010-0000-0079');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('80a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a80@mail.com','참가자80', 1, '010-0000-0080');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('81a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a81@mail.com','참가자81', 1, '010-0000-0081');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('82a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a82@mail.com','참가자82', 1, '010-0000-0082');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('83a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a83@mail.com','참가자83', 1, '010-0000-0083');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('84a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a84@mail.com','참가자84', 1, '010-0000-0084');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('85a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a85@mail.com','참가자85', 1, '010-0000-0085');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('86a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a86@mail.com','참가자86', 1, '010-0000-0086');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('87a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a87@mail.com','참가자87', 1, '010-0000-0087');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('88a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a88@mail.com','참가자88', 1, '010-0000-0088');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('89a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a89@mail.com','참가자89', 1, '010-0000-0089');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('90a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a90@mail.com','참가자90', 1, '010-0000-0090');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('91a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a91@mail.com','참가자91', 1, '010-0000-0091');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('92a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a92@mail.com','참가자92', 1, '010-0000-0092');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('93a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a93@mail.com','참가자93', 1, '010-0000-0093');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('94a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a94@mail.com','참가자94', 1, '010-0000-0094');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('95a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a95@mail.com','참가자95', 1, '010-0000-0095');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('96a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a96@mail.com','참가자96', 1, '010-0000-0096');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('97a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a97@mail.com','참가자97', 1, '010-0000-0097');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('98a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a98@mail.com','참가자98', 1, '010-0000-0098');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('99a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a99@mail.com','참가자99', 1, '010-0000-0099');

insert into members(id, password, provider, role, email, name, profile_image_number, tel)
values ('100a', '{noop}123qwe!@#', 'GOOGLE', 'ROLE_USER', 'a100@mail.com','참가자100', 1, '010-0000-0100');



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

insert into group_members(id, role, member_id, team_id, group_admin)
values (10011, 'GROUP_MEMBER', '11a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10012, 'GROUP_MEMBER', '12a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10013, 'GROUP_MEMBER', '13a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10014, 'GROUP_MEMBER', '14a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10015, 'GROUP_MEMBER', '15a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10016, 'GROUP_MEMBER', '16a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10017, 'GROUP_MEMBER', '17a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10018, 'GROUP_MEMBER', '18a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10019, 'GROUP_MEMBER', '19a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10020, 'GROUP_MEMBER', '20a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10021, 'GROUP_MEMBER', '21a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10022, 'GROUP_MEMBER', '22a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10023, 'GROUP_MEMBER', '23a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10024, 'GROUP_MEMBER', '24a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10025, 'GROUP_MEMBER', '25a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10026, 'GROUP_MEMBER', '26a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10027, 'GROUP_MEMBER', '27a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10028, 'GROUP_MEMBER', '28a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10029, 'GROUP_MEMBER', '29a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10030, 'GROUP_MEMBER', '30a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10031, 'GROUP_MEMBER', '31a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10032, 'GROUP_MEMBER', '32a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10033, 'GROUP_MEMBER', '33a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10034, 'GROUP_MEMBER', '34a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10035, 'GROUP_MEMBER', '35a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10036, 'GROUP_MEMBER', '36a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10037, 'GROUP_MEMBER', '37a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10038, 'GROUP_MEMBER', '38a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10039, 'GROUP_MEMBER', '39a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10040, 'GROUP_MEMBER', '40a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10041, 'GROUP_MEMBER', '41a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10042, 'GROUP_MEMBER', '42a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10043, 'GROUP_MEMBER', '43a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10044, 'GROUP_MEMBER', '44a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10045, 'GROUP_MEMBER', '45a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10046, 'GROUP_MEMBER', '46a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10047, 'GROUP_MEMBER', '47a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10048, 'GROUP_MEMBER', '48a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10049, 'GROUP_MEMBER', '49a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10050, 'GROUP_MEMBER', '50a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10051, 'GROUP_MEMBER', '51a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10052, 'GROUP_MEMBER', '52a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10053, 'GROUP_MEMBER', '53a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10054, 'GROUP_MEMBER', '54a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10055, 'GROUP_MEMBER', '55a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10056, 'GROUP_MEMBER', '56a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10057, 'GROUP_MEMBER', '57a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10058, 'GROUP_MEMBER', '58a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10059, 'GROUP_MEMBER', '59a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10060, 'GROUP_MEMBER', '60a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10061, 'GROUP_MEMBER', '61a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10062, 'GROUP_MEMBER', '62a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10063, 'GROUP_MEMBER', '63a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10064, 'GROUP_MEMBER', '64a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10065, 'GROUP_MEMBER', '65a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10066, 'GROUP_MEMBER', '66a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10067, 'GROUP_MEMBER', '67a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10068, 'GROUP_MEMBER', '68a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10069, 'GROUP_MEMBER', '69a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10070, 'GROUP_MEMBER', '70a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10071, 'GROUP_MEMBER', '71a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10072, 'GROUP_MEMBER', '72a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10073, 'GROUP_MEMBER', '73a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10074, 'GROUP_MEMBER', '74a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10075, 'GROUP_MEMBER', '75a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10076, 'GROUP_MEMBER', '76a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10077, 'GROUP_MEMBER', '77a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10078, 'GROUP_MEMBER', '78a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10079, 'GROUP_MEMBER', '79a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10080, 'GROUP_MEMBER', '80a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10081, 'GROUP_MEMBER', '81a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10082, 'GROUP_MEMBER', '82a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10083, 'GROUP_MEMBER', '83a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10084, 'GROUP_MEMBER', '84a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10085, 'GROUP_MEMBER', '85a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10086, 'GROUP_MEMBER', '86a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10087, 'GROUP_MEMBER', '87a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10088, 'GROUP_MEMBER', '88a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10089, 'GROUP_MEMBER', '89a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10090, 'GROUP_MEMBER', '90a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10091, 'GROUP_MEMBER', '91a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10092, 'GROUP_MEMBER', '92a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10093, 'GROUP_MEMBER', '93a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10094, 'GROUP_MEMBER', '94a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10095, 'GROUP_MEMBER', '95a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10096, 'GROUP_MEMBER', '96a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10097, 'GROUP_MEMBER', '97a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10098, 'GROUP_MEMBER', '98a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10099, 'GROUP_MEMBER', '99a', 1, false);

insert into group_members(id, role, member_id, team_id, group_admin)
values (10100, 'GROUP_MEMBER', '100a', 1, false);

