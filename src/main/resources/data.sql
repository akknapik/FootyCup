-- USER --
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (1, 'Ethan', 'Walker', 'ewalker@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'ADMIN', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (2, 'Liam', 'Hall', 'lhall@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (3, 'John', 'Smith', 'jsmith@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (4, 'Emily', 'Johnson', 'ejohnson@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (5, 'Michael', 'Williams', 'mwilliams@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (6, 'Emma', 'Brown', 'ebrown@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (7, 'Daniel', 'Jones', 'djones@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (8, 'Olivia', 'Garcia', 'ogarcia@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (9, 'Matthew', 'Miller', 'mmiller@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (10, 'Sophia', 'Davis', 'sdavis@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (11, 'James', 'Martinez', 'jmartinez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (12, 'Ava', 'Hernandez', 'ahernandez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (13, 'William', 'Lopez', 'wlopez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (id, firstname, lastname, email, password, user_role, locked, enabled)
VALUES (14, 'Isabella', 'Gonzalez', 'igonzalez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);

-- TOURNAMENT --
INSERT INTO tournaments (id, name, start_date, end_date, location, id_organizer, status) VALUES (1, 'Global Masters Series', '2025-06-10', '2025-06-13', 'New York, USA', 1, 'UPCOMING');

-- SCHEDULE --
INSERT INTO schedules (id, id_tournament, start_date_time, break_between_matches_in_min) VALUES (1, 1, '2025-06-10T09:00:00', 0);
INSERT INTO schedules (id, id_tournament, start_date_time, break_between_matches_in_min) VALUES (2, 1, '2025-06-11T09:00:00', 0);
INSERT INTO schedules (id, id_tournament, start_date_time, break_between_matches_in_min) VALUES (3, 1, '2025-06-12T09:00:00', 0);
INSERT INTO schedules (id, id_tournament, start_date_time, break_between_matches_in_min) VALUES (4, 1, '2025-06-13T09:00:00', 0);

-- TEAM --
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (1, 'Redhawks', 'Cyprus', 1, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (2, 'Ironwolves', 'Luxembourg', 2, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (3, 'Blazefury', 'Paraguay', 3, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (4, 'Stormriders', 'Antigua and Barbuda', 4, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (5, 'Shadowfox', 'Norway', 5, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (6, 'Vortex', 'Denmark', 6, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (7, 'Thunderstrike', 'Netherlands', 7, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (8, 'Frostbite', 'Poland', 8, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (9, 'Steelhorns', 'Sweden', 9, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (10, 'Nightcrawlers', 'Iceland', 10, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (11, 'Skyblades', 'Finland', 11, 1);
INSERT INTO teams (id, name, country, id_coach, id_tournament) VALUES (12, 'Bluefang', 'Ireland', 12, 1);

-- PLAYER --
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (1, 'Michael Walls', 14, '2002-06-27', 1);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (2, 'Lynn Palmer', 15, '2006-05-31', 1);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (3, 'Molly Hill', 99, '2004-10-16', 1);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (4, 'Paul Smith', 94, '1995-08-19', 2);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (5, 'Brittany Oliver', 70, '2006-09-27', 2);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (6, 'Donna Lewis', 53, '2000-01-13', 2);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (7, 'Jennifer Parker', 43, '1995-01-02', 2);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (8, 'Casey Wright', 2, '1999-09-14', 2);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (9, 'Frank Thompson', 14, '1990-05-15', 3);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (10, 'Nicole Hunter', 55, '1991-04-03', 3);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (11, 'Joel Rivera', 66, '2004-11-12', 3);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (12, 'Joshua Morris', 38, '2001-10-25', 3);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (13, 'Diana Morris', 65, '1991-08-22', 4);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (14, 'Angela Tucker', 79, '2002-09-29', 4);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (15, 'Joshua Powell', 89, '1993-10-11', 4);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (16, 'Colleen Bryant', 35, '2000-04-26', 4);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (17, 'Renee Elliott', 80, '1991-05-24', 5);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (18, 'Daniel Hayes', 4, '1997-11-20', 5);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (19, 'Terry Rogers', 60, '2004-05-09', 5);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (20, 'Sharon Edwards', 49, '1992-02-10', 5);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (21, 'Jared Riley', 46, '2005-08-14', 5);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (22, 'Nicole Mills', 91, '2003-12-07', 6);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (23, 'Misty Franklin', 96, '1993-06-06', 6);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (24, 'Michael Page', 68, '2002-01-09', 6);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (25, 'Douglas Anderson', 3, '1990-08-01', 7);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (26, 'Jeffery Warren', 25, '2003-04-14', 7);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (27, 'Jill Jackson', 12, '2001-07-12', 7);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (28, 'James Peters', 88, '1997-02-01', 7);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (29, 'Jason James', 61, '1993-04-18', 7);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (30, 'Anthony Wright', 17, '1991-03-30', 8);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (31, 'Richard Sullivan', 28, '1990-06-11', 8);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (32, 'Carolyn Rodriguez', 32, '2002-07-08', 8);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (33, 'Laura Ramirez', 93, '1992-09-06', 8);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (34, 'Jonathan Ross', 35, '2002-12-05', 9);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (35, 'Tracy Morales', 31, '1995-01-31', 9);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (36, 'Micheal Patterson', 33, '1990-03-29', 9);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (37, 'Kaitlyn Jensen', 72, '2001-06-18', 10);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (38, 'Jeremiah Foster', 59, '2002-04-22', 10);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (39, 'Sara Carter', 82, '2000-10-23', 10);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (40, 'Zachary Holmes', 76, '1996-11-07', 11);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (41, 'Debra Shelton', 98, '1991-02-06', 11);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (42, 'Chad Bryant', 84, '1994-09-04', 11);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (43, 'Stephanie Sanders', 97, '1993-01-24', 11);

INSERT INTO players (id, name, number, birth_date, id_team) VALUES (44, 'Angela Henry', 9, '2003-03-17', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (45, 'Nancy Nguyen', 20, '1996-12-02', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (46, 'Tyler Gray', 42, '1999-04-19', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (47, 'Bobby Flores', 73, '1992-05-25', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (48, 'Peter Green', 24, '2001-08-30', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (49, 'Rita Cooper', 10, '1995-06-21', 12);
INSERT INTO players (id, name, number, birth_date, id_team) VALUES (50, 'Shawn Bell', 45, '1994-10-02', 12);
