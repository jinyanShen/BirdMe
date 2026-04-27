-- BirdME Database Schema

-- Create users table
CREATE TABLE IF NOT EXISTS user (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    username VARCHAR(50) NOT NULL UNIQUE,
                                    password VARCHAR(100) NOT NULL,
                                    name VARCHAR(50),
                                    age INTEGER,
                                    phone VARCHAR(20),
                                    avatar_url VARCHAR(255),
                                    role VARCHAR(20) NOT NULL DEFAULT 'user',
                                    rescue_station_id INTEGER,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create rescue_stations table
CREATE TABLE IF NOT EXISTS rescue_station (
                                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                                               name VARCHAR(100) NOT NULL,
                                               address VARCHAR(200) NOT NULL,
                                               latitude DOUBLE NOT NULL,
                                               longitude DOUBLE NOT NULL,
                                               phone VARCHAR(20) NOT NULL,
                                               email VARCHAR(100),
                                               opening_hours VARCHAR(100),
                                               description TEXT,
                                               contact_person VARCHAR(50)
);

-- Create reports table
CREATE TABLE IF NOT EXISTS report (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       bird_name VARCHAR(100) NOT NULL,
                                       species VARCHAR(100),
                                       latitude DOUBLE NOT NULL,
                                       longitude DOUBLE NOT NULL,
                                       location VARCHAR(200),
                                       injury_type VARCHAR(50) NOT NULL,
                                       injury_description TEXT,
                                       image_url VARCHAR(200),
                                       status VARCHAR(20) DEFAULT 'PENDING',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       submitter_id VARCHAR(50) NOT NULL,
                                       rescue_station_id INTEGER,
                                       notes TEXT,
                                       FOREIGN KEY (rescue_station_id) REFERENCES rescue_station(id)
);

-- Insert sample rescue stations
INSERT INTO rescue_station (name, address, latitude, longitude, phone, email, opening_hours, description, contact_person)
VALUES
    ('Wildlife Rescue Center', '123 Main St, City Center', 40.7128, -74.0060, '123-456-7890', 'info@wildliferescue.org', '9am-5pm daily', 'Professional wildlife rescue and rehabilitation center', 'John Smith'),
    ('Bird Rescue Sanctuary', '456 Oak Ave, Suburb', 34.0522, -118.2437, '987-654-3210', 'contact@birdsanctuary.org', '10am-4pm daily', 'Specialized in bird rescue and rehabilitation', 'Jane Doe'),
    ('Animal Welfare League', '789 Pine Rd, Countryside', 41.8781, -87.6298, '555-123-4567', 'support@animalwelfare.org', '8am-6pm daily', 'Comprehensive animal welfare organization', 'Mike Johnson');

-- Insert sample users
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(1, 'user1', '123456', 0, 'Zhang San', 25, '13811000002', 'http://localhost:8080/file/download?id=cb7cd1ec17624b27bafc3b63a53310a4', '1773816096000');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(2, 'user2', '123456', 0, 'Li Si', 30, '13800000002', 'http://localhost:8080/file/download?id=bc0a475b0d7d4fbeac5106d2015f631c', '1773816096000');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(3, 'admin', '123456', 1, 'Admin', 35, '13800000003', 'http://localhost:8080/file/download?id=0c58187449134bf8ac4bbeaeda45da1b', '1773816096000');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(4, 'normaluser3', '123456', 0, 'Wang Wu', 28, '13800000004', 'http://localhost:8080/file/download?id=0c43b993aca446f195c3821c712f3838', '1773816096000');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(5, 'user4', '123456', 0, 'Zhao Liu', 22, '13800000005', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(6, 'admin2', '123456', 1, 'Admin 2', 40, '13800000006', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(7, 'newuser5', '123456', 0, 'Sun Qi', 27, '13800000007', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(8, 'user6', '123456', 0, 'Zhou Ba', 32, '13800000008', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(9, 'user7', '123456', 0, 'Wu Jiu', 29, '13800000009', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(10, 'user8', '123456', 0, 'Zheng Shi', 24, '13800000010', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', '2026-03-18 14:41:36');
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, created_at)
VALUES(12, 'user11', '123456', 0, '111', 11, '11', '', '2026-03-29 14:20:25');

-- Insert rescue station users (role=2)
INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, rescue_station_id, created_at)
VALUES(20, 'rescue1', '123456', 2, 'Wildlife Rescue Center Manager', 35, '13900000001', '', 1, '2026-04-14 10:00:00');

INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, rescue_station_id, created_at)
VALUES(21, 'rescue2', '123456', 2, 'Bird Rescue Sanctuary Manager', 28, '13900000002', '', 2, '2026-04-14 10:00:00');

INSERT INTO "user"
(id, username, password, "role", name, age, phone, avatar_url, rescue_station_id, created_at)
VALUES(22, 'rescue3', '123456', 2, 'Animal Welfare League Manager', 42, '13900000003', '', 3, '2026-04-14 10:00:00');



-- Report table insert statements
INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Sparrow', 'Passer domesticus', 39.9042, 116.4074, 'Central Park', 'Broken wing', 'The bird has a broken wing and cannot fly', NULL, 'PENDING', datetime('now'), datetime('now'), 'user', 1, 'Found near the park entrance');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Pigeon', 'Columba livia', 39.9142, 116.4174, 'City Square', 'Foot injury', 'The bird has an injured foot and cannot stand properly', NULL, 'PROCESSING', datetime('now'), datetime('now'), 'user', 2, 'Found near the fountain');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Crow', 'Corvus brachyrhynchos', 39.9242, 116.4274, 'Riverside Park', 'Head injury', 'The bird has a head injury and appears disoriented', NULL, 'COMPLETED', datetime('now'), datetime('now'), 'user', 1, 'Found near the river bank');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Robin', 'Turdus migratorius', 39.9342, 116.4374, 'Suburban Area', 'Starvation', 'The bird is weak and malnourished', NULL, 'PENDING', datetime('now'), datetime('now'), 'user', 2, 'Found in a backyard');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Hawk', 'Buteo jamaicensis', 39.9442, 116.4474, 'Mountain Area', 'Poisoning', 'The bird appears to have been poisoned', NULL, 'PROCESSING', datetime('now'), datetime('now'), 'user', 1, 'Found near a farm');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Eagle', 'Aquila chrysaetos', 39.9542, 116.4574, 'Forest Area', 'Wing injury', 'The bird has a damaged wing feather', NULL, 'CANCELLED', datetime('now'), datetime('now'), 'user', 2, 'Found in the forest');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Owl', 'Bubo virginianus', 39.9642, 116.4674, 'Woodland Park', 'Eye injury', 'The bird has an injured eye', NULL, 'PENDING', datetime('now'), datetime('now'), 'user', 1, 'Found near the old oak tree');

INSERT INTO report (bird_name, species, latitude, longitude, location, injury_type, injury_description, image_url, status, created_at, updated_at, submitter_id, rescue_station_id, notes)
VALUES ('Duck', 'Anas platyrhynchos', 39.9742, 116.4774, 'Lake Side', 'Leg injury', 'The bird has a broken leg', NULL, 'PROCESSING', datetime('now'), datetime('now'), 'user', 2, 'Found near the lake shore');

-- Create forum_post table
CREATE TABLE IF NOT EXISTS forum_post (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    tag VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    author_id INTEGER NOT NULL,
    author_name VARCHAR(50) NOT NULL,
    author_avatar VARCHAR(255),
    is_pinned INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    reply_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES user(id)
);

-- Create forum_reply table
CREATE TABLE IF NOT EXISTS forum_reply (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    author_id INTEGER NOT NULL,
    author_name VARCHAR(50) NOT NULL,
    author_avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_post(id),
    FOREIGN KEY (author_id) REFERENCES user(id)
);

-- Insert sample forum posts (delete existing data first to avoid duplicates)
DELETE FROM forum_post;
DELETE FROM forum_reply;

INSERT INTO forum_post (title, content, tag, category, author_id, author_name, author_avatar, is_pinned, view_count, reply_count) VALUES 
('Best Bird Watching Spots in Spring', 'Hello everyone! Spring is here. I want to share some of my favorite bird watching locations. First is the Wetland Park in the city center, where many migratory birds gather...', 'Bird Watching', 'birdwatching', 1, 'Zhang San', 'http://localhost:8080/file/download?id=cb7cd1ec17624b27bafc3b63a53310a4', 1, 120, 15),
('How to Distinguish Sparrows from Tits?', 'Newbie here! I saw two types of small birds in the park that look very similar. How can I tell them apart? Any experts can help?', 'Bird Watching', 'birdwatching', 2, 'Li Si', 'http://localhost:8080/file/download?id=bc0a475b0d7d4fbeac5106d2015f631c', 0, 85, 8),
('Important Notes on Keeping Kestrels', 'Some friends asked if kestrels can be kept as pets. Let me clarify: Kestrels are national second-class protected animals, and private ownership is illegal...', 'Education', 'birdwatching', 3, 'Admin', 'http://localhost:8080/file/download?id=0c58187449134bf8ac4bbeaeda45da1b', 1, 256, 32),
('Captured Beautiful Egret Photos!', 'Took these photos at the lake today. Sharing with everyone! Equipment: Canon R5 + 100-400mm lens', 'Photography', 'birdwatching', 4, 'Wang Wu', 'http://localhost:8080/file/download?id=0c43b993aca446f195c3821c712f3838', 0, 178, 22),
('Help: What to Do with Injured Bird?', 'Found an injured bird on my balcony, seems like its wing is hurt. What should I do?', 'Help', 'qa', 5, 'Zhao Liu', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', 0, 45, 6),
('Bird Migration Routes Explained', 'Every spring and autumn, billions of birds migrate long distances. There are three main bird migration routes in China...', 'Education', 'birdwatching', 3, 'Admin', 'http://localhost:8080/file/download?id=0c58187449134bf8ac4bbeaeda45da1b', 0, 312, 41),
('What Bird Is This? Need Help!', 'Found this in my neighborhood tree, it sings beautifully. Want to know what species it is', 'Identification', 'qa', 6, 'Admin 2', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', 0, 67, 12),
('Recommend Some Bird Watching Binoculars', 'Looking to buy bird watching binoculars, budget around 2000 yuan. Any recommendations?', 'Equipment', 'birdwatching', 7, 'Sun Qi', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', 0, 93, 18);

-- Insert sample forum replies
INSERT INTO forum_reply (post_id, content, author_id, author_name, author_avatar) VALUES
(1, 'Great recommendations! I also suggest visiting the Nature Reserve in the suburbs.', 2, 'Li Si', 'http://localhost:8080/file/download?id=bc0a475b0d7d4fbeac5106d2015f631c'),
(1, 'Thanks for sharing! I will visit this weekend.', 3, 'Admin', 'http://localhost:8080/file/download?id=0c58187449134bf8ac4bbeaeda45da1b'),
(2, 'Sparrows are usually brown with streaks, while tits have more colorful plumage with blue or yellow.', 1, 'Zhang San', 'http://localhost:8080/file/download?id=cb7cd1ec17624b27bafc3b63a53310a4'),
(3, 'Very informative! Many people dont know this.', 4, 'Wang Wu', 'http://localhost:8080/file/download?id=0c43b993aca446f195c3821c712f3838'),
(5, 'Please contact local wildlife rescue center immediately! Keep the bird warm and quiet.', 3, 'Admin', 'http://localhost:8080/file/download?id=0c58187449134bf8ac4bbeaeda45da1b'),
(5, 'I had similar experience. The rescue center was very helpful.', 1, 'Zhang San', 'http://localhost:8080/file/download?id=cb7cd1ec17624b27bafc3b63a53310a4'),
(7, 'Looks like a Chinese Bulbul! They have distinctive crests.', 2, 'Li Si', 'http://localhost:8080/file/download?id=bc0a475b0d7d4fbeac5106d2015f631c'),
(8, 'I recommend the Nikon Aculon series, good value for money.', 5, 'Zhao Liu', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9');