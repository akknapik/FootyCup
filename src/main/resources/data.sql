-- USERS --

INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Ethan', 'Walker', 'ewalker@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'ADMIN', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Liam', 'Hall', 'lhall@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('John', 'Smith', 'jsmith@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Emily', 'Johnson', 'ejohnson@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Michael', 'Williams', 'mwilliams@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Emma', 'Brown', 'ebrown@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Daniel', 'Jones', 'djones@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Olivia', 'Garcia', 'ogarcia@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Matthew', 'Miller', 'mmiller@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Sophia', 'Davis', 'sdavis@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('James', 'Martinez', 'jmartinez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Ava', 'Hernandez', 'ahernandez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('William', 'Lopez', 'wlopez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);
INSERT INTO users (firstname, lastname, email, password, user_role, locked, enabled)
VALUES ('Isabella', 'Gonzalez', 'igonzalez@example.com', '$2a$10$YuYkTMf3ZwEA/8DcwqqjxeYD8QSw5HGhl7Vhnj1DtCxCkvzXxMMNi', 'USER', false, false);

-- TOURNAMENT --
INSERT INTO tournaments (name, start_date, end_date, location, id_organizer, status, is_public, qr_code_generated)
VALUES ('Global Masters Series', '2025-11-05', '2025-11-08', 'New York, USA', 1, 'UPCOMING', TRUE, FALSE);

-- SCHEDULE --
INSERT INTO schedules (id_tournament, start_date_time, break_between_matches_in_min)
VALUES (1, '2025-11-05T09:00:00', 0);

INSERT INTO schedules (id_tournament, start_date_time, break_between_matches_in_min)
VALUES (1, '2025-11-06T09:00:00', 0);

INSERT INTO schedules (id_tournament, start_date_time, break_between_matches_in_min)
VALUES (1, '2025-11-07T09:00:00', 0);

INSERT INTO schedules (id_tournament, start_date_time, break_between_matches_in_min)
VALUES (1, '2025-11-08T09:00:00', 0);

-- TEAM --
INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Redhawks', 'Cyprus', 1, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Ironwolves', 'Luxembourg', 2, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Blazefury', 'Paraguay', 3, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Stormriders', 'Antigua and Barbuda', 4, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Shadowfox', 'Norway', 5, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Vortex', 'Denmark', 6, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Thunderstrike', 'Netherlands', 7, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Frostbite', 'Poland', 8, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Steelhorns', 'Sweden', 9, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Nightcrawlers', 'Iceland', 10, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Skyblades', 'Finland', 11, 1);

INSERT INTO teams (name, country, id_coach, id_tournament)
VALUES ('Bluefang', 'Ireland', 12, 1);

-- PLAYER --
INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Michael Walls', 14, '2002-06-27', 1);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Lynn Palmer', 15, '2006-05-31', 1);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Molly Hill', 99, '2004-10-16', 1);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Paul Smith', 94, '1995-08-19', 2);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Brittany Oliver', 70, '2006-09-27', 2);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Donna Lewis', 53, '2000-01-13', 2);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jennifer Parker', 43, '1995-01-02', 2);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Casey Wright', 2, '1999-09-14', 2);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Frank Thompson', 14, '1990-05-15', 3);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Nicole Hunter', 55, '1991-04-03', 3);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Joel Rivera', 66, '2004-11-12', 3);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Joshua Morris', 38, '2001-10-25', 3);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Diana Morris', 65, '1991-08-22', 4);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Angela Tucker', 79, '2002-09-29', 4);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Joshua Powell', 89, '1993-10-11', 4);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Colleen Bryant', 35, '2000-04-26', 4);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Renee Elliott', 80, '1991-05-24', 5);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Daniel Hayes', 4, '1997-11-20', 5);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Terry Rogers', 60, '2004-05-09', 5);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Sharon Edwards', 49, '1992-02-10', 5);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jared Riley', 46, '2005-08-14', 5);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Nicole Mills', 91, '2003-12-07', 6);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Misty Franklin', 96, '1993-06-06', 6);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Michael Page', 68, '2002-01-09', 6);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Douglas Anderson', 3, '1990-08-01', 7);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jeffery Warren', 25, '2003-04-14', 7);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jill Jackson', 12, '2001-07-12', 7);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('James Peters', 88, '1997-02-01', 7);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jason James', 61, '1993-04-18', 7);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Anthony Wright', 17, '1991-03-30', 8);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Richard Sullivan', 28, '1990-06-11', 8);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Carolyn Rodriguez', 32, '2002-07-08', 8);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Laura Ramirez', 93, '1992-09-06', 8);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jonathan Ross', 35, '2002-12-05', 9);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Tracy Morales', 31, '1995-01-31', 9);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Micheal Patterson', 33, '1990-03-29', 9);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Kaitlyn Jensen', 72, '2001-06-18', 10);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Jeremiah Foster', 59, '2002-04-22', 10);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Sara Carter', 82, '2000-10-23', 10);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Zachary Holmes', 76, '1996-11-07', 11);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Debra Shelton', 98, '1991-02-06', 11);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Chad Bryant', 84, '1994-09-04', 11);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Stephanie Sanders', 97, '1993-01-24', 11);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Angela Henry', 9, '2003-03-17', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Nancy Nguyen', 20, '1996-12-02', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Tyler Gray', 42, '1999-04-19', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Bobby Flores', 73, '1992-05-25', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Peter Green', 24, '2001-08-30', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Rita Cooper', 10, '1995-06-21', 12);

INSERT INTO players (name, number, birth_date, id_team)
VALUES ('Shawn Bell', 45, '1994-10-02', 12);
