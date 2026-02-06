INSERT INTO users (username, email, password_hash, role)
VALUES ('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3QYNXK1S7E6qr5MHJz.K', 'ADMIN');

INSERT INTO games (name, slug, description)
VALUES  ('Minecraft', 'minecraft', 'Build and explore infinite worlds'),
        ('The Witcher 3', 'witcher3', 'Epic open-world RPG adventure'),
        ('Stardew Valley', 'stardew-valley', 'Farming simulation RPG'),
        ('Terraria', 'terraria', '2D sandbox adventure game'),
        ('Hollow Knight', 'hollow-knight', 'Metroidvania action-adventure');