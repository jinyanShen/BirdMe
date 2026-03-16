create database birdme;
use birdme;

-- User table
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'User unique ID',
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT 'Username (unique)',
  `password` VARCHAR(255) NOT NULL COMMENT 'User password',
  `role` TINYINT DEFAULT 0 COMMENT 'User role 0-regular user, 1-admin',
  `name` VARCHAR(100) NOT NULL COMMENT 'User real name',
  `age` INT DEFAULT 0 COMMENT 'User age',
  `phone` VARCHAR(20) UNIQUE NOT NULL COMMENT 'User phone number (unique)',
  `avatar_url` VARCHAR(255) COMMENT 'User avatar URL',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` (`username`, `password`, `role`, `name`, `age`, `phone`, `avatar_url`, `created_at`) VALUES
('user1', '123456', 0, 'Zhang San', 25, '13800000001', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', NOW()),
('user2', '123456', 1, 'Li Si', 30, '13800000002', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', NOW()),
('admin', '123456', 1, 'Admin', 35, '13800000003', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', NOW()),
('normaluser3', '123456', 0, 'Wang Wu', 28, '13800000004', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', NOW()),
('user4', '123456', 0, 'Zhao Liu', 22, '13800000005', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', NOW()),
('admin2', '123456', 1, 'Admin 2', 40, '13800000006', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', NOW()),
('newuser5', '123456', 0, 'Sun Qi', 27, '13800000007', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', NOW()),
('user6', '123456', 0, 'Zhou Ba', 32, '13800000008', 'http://localhost:8080/file/download?id=08912f72952e42d1a977378f68ecb2e9', NOW()),
('user7', '123456', 0, 'Wu Jiu', 29, '13800000009', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', NOW()),
('user8', '123456', 0, 'Zheng Shi', 24, '13800000010', 'http://localhost:8080/file/download?id=9f4ed887ce9a4093b9259ecf900c4f71', NOW());