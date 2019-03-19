insert into user (ID,NAME,PASSWORD,ROLE) values (1,'TestUser','$2a$11$Qh.bpyGjATn9IBjlIWBJN.rGdl366V2IyQsMwWcc8hN3lHKm8.tqu',1);
insert into user (ID,NAME,PASSWORD,ROLE) values (2,'TestManager','$2a$11$Qh.bpyGjATn9IBjlIWBJN.rGdl366V2IyQsMwWcc8hN3lHKm8.tqu',2);
insert into user (ID,NAME,PASSWORD,ROLE) values (3,'TestAdmin','$2a$11$Qh.bpyGjATn9IBjlIWBJN.rGdl366V2IyQsMwWcc8hN3lHKm8.tqu',3);

insert into permission (ID,NAME,ROLE) values (1,'user',1);
insert into permission (ID,NAME,ROLE) values (2,'manager',2);
insert into permission (ID,NAME,ROLE) values (3,'user',2);
insert into permission (ID,NAME,ROLE) values (4,'admin',3);
insert into permission (ID,NAME,ROLE) values (5,'manager',3);
insert into permission (ID,NAME,ROLE) values (6,'user',3);

insert into time_entry (id, description, start_time, duration, user_id) values (1, 'description 1', TIMESTAMP '2019-01-31 12:00:00', 30, 1);
insert into time_entry (id, description, start_time, duration, user_id) values (2, 'description 1', TIMESTAMP '2019-01-01 16:00:00', 50, 1);
insert into time_entry (id, description, start_time, duration, user_id) values (3, 'description 1', TIMESTAMP '2019-01-31 20:00:00', 20, 1);

insert into time_entry (id, description, start_time, duration, user_id) values (4, 'description 4', TIMESTAMP '2019-01-13 12:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (5, 'description 5', TIMESTAMP '2019-01-17 09:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (6, 'description 6', TIMESTAMP '2019-01-15 08:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (7, 'description 7', TIMESTAMP '2019-01-16 11:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (8, 'description 8', TIMESTAMP '2019-01-16 12:22:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (9, 'description 9', TIMESTAMP '2019-01-16 20:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (10, 'description 10', TIMESTAMP '2019-01-19 12:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (11, 'description 11', TIMESTAMP '2019-01-11 11:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (12, 'description 12', TIMESTAMP '2019-01-01 10:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (13, 'description 4', TIMESTAMP '2019-01-13 12:00:00', 130, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (14, 'description 5', TIMESTAMP '2019-01-14 12:00:00', 30, 2);

insert into time_entry (id, description, start_time, duration, user_id) values (15, 'description 6', TIMESTAMP '2019-01-15 11:00:00', 130, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (16, 'description 7', TIMESTAMP '2019-01-16 14:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (17, 'description 8', TIMESTAMP '2019-01-16 15:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (18, 'description 9', TIMESTAMP '2019-01-16 16:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (19, 'description 10', TIMESTAMP '2019-01-19 11:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (20, 'description 4', TIMESTAMP '2019-01-13 09:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (21, 'description 5', TIMESTAMP '2019-01-14 09:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (22, 'description 6', TIMESTAMP '2019-01-15 10:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (23, 'description 7', TIMESTAMP '2019-01-16 13:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (24, 'description 8', TIMESTAMP '2019-01-16 14:30:00', 30, 2);

insert into time_entry (id, description, start_time, duration, user_id) values (25, 'description 9', TIMESTAMP '2019-01-19 15:00:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (26, 'description 10', TIMESTAMP '2019-01-19 15:30:00', 30, 2);
insert into time_entry (id, description, start_time, duration, user_id) values (27, 'description 10', TIMESTAMP '2019-01-19 15:30:00', 30, 3);

insert into user_setting (id, start_time, end_time, user_id) values (1, TIME '12:00:00', TIME '13:00:00', 1);
insert into user_setting (id, start_time, end_time, user_id) values (2, TIME '10:00:00', TIME '14:00:00', 2);
