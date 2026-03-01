INSERT INTO users (username, email, password_hash, role)
VALUES ('admin', 'admin@example.com', '$2a$12$1GaEpzwNLkByWz6m9cm47.3f898Rc6BDS8tN8efj/lkMwbqRMgTma', 'ADMIN');

INSERT INTO games (name, slug, description)
VALUES  ('Minecraft', 'minecraft', 'Build and explore infinite worlds'),
        ('The Witcher 3', 'witcher3', 'Epic open-world RPG adventure'),
        ('Stardew Valley', 'stardew-valley', 'Farming simulation RPG'),
        ('Terraria', 'terraria', '2D sandbox adventure game'),
        ('Hollow Knight', 'hollow-knight', 'Metroidvania action-adventure');