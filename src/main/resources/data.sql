-- Użytkownik
INSERT INTO users (id, firstname, lastname, email, password, userRole) VALUES
    (1, 'Jan', 'Kowalski', 'jkowal@gmail.com', '$2a$10$xkjZFXNYVZK31VK.PZQWNe1ZcJrE4gJGJIoJGE7lOS2.KZj9hF25e', USER);

-- Turniej
INSERT INTO tournaments (id, name, start_date, end_date, location, id_organizer, status) VALUES
    (1, 'Turniej Testowy', '2025-06-01', '2025-06-10', 'Warszawa', 1, 'UPCOMING');

-- Drużyny
INSERT INTO teams (id, name, id_coach, country, id_tournament) VALUES
                                                                   (1, 'Drużyna 1', 1, 'Polska', 1),
                                                                   (2, 'Drużyna 2', 1, 'Niemcy', 1),
                                                                   (3, 'Drużyna 3', 1, 'Hiszpania', 1),
                                                                   (4, 'Drużyna 4', 1, 'Francja', 1),
                                                                   (5, 'Drużyna 5', 1, 'Anglia', 1),
                                                                   (6, 'Drużyna 6', 1, 'Włochy', 1),
                                                                   (7, 'Drużyna 7', 1, 'Portugalia', 1),
                                                                   (8, 'Drużyna 8', 1, 'Holandia', 1),
                                                                   (9, 'Drużyna 9', 1, 'Belgia', 1),
                                                                   (10, 'Drużyna 10', 1, 'Chorwacja', 1),
                                                                   (11, 'Drużyna 11', 1, 'Szwecja', 1),
                                                                   (12, 'Drużyna 12', 1, 'Norwegia', 1);
