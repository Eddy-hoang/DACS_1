-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: May 29, 2025 at 10:40 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `employee`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `password`, `role`) VALUES
('manager', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'manager'),
('employee', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'employee');

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
  `id` int NOT NULL,
  `employee_id` varchar(100) NOT NULL,
  `firstName` varchar(100) NOT NULL,
  `lastName` varchar(100) NOT NULL,
  `gender` varchar(100) NOT NULL,
  `phoneNum` varchar(100) NOT NULL,
  `position` varchar(100) NOT NULL,
  `image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`id`, `employee_id`, `firstName`, `lastName`, `gender`, `phoneNum`, `position`, `image`, `date`) VALUES
(26, '1', 'Hoàng', 'Nghĩa', 'Male', '092737', 'Marketer Coordinator', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\460204898_1044188594375137_6202268670278737940_n.jpg', '2025-05-24'),
(27, '2', 'Trung', 'Đỗ', 'Male', '0294729', 'Web Developer(Back End)', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\480359134_598118713032757_2694751198289396523_n.jpg', '2025-05-24'),
(28, '3', 'Eddy', 'Hoang', 'Female', '0826492', 'Web Developer(Font End)', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Desktop\\\\cntt\\\\495228747_1217602670042177_106440692544997892_n.jpg', '2025-05-24'),
(29, '4', 'Nguyễn', 'Điền', 'Male', '09274', 'Marketer Coordinator', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\460204898_1044188594375137_6202268670278737940_n.jpg', '2025-05-25'),
(33, '5', 'Hồ', 'Huy', 'Male', '993694', 'Web Developer(Back End)', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\480359134_598118713032757_2694751198289396523_n.jpg', '2025-05-27'),
(34, '6', 'Tết', 'tét', 'Others', '820', 'Web Developer(Back End)', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\Screenshots\\\\Ảnh chụp màn hình 2024-09-11 134322.png', '2025-05-27'),
(35, '7', 'Test', 'test', 'Others', '43831092', 'Marketer Coordinator', 'C:\\\\Users\\\\LENOVO\\\\OneDrive\\\\Pictures\\\\Screenshots\\\\Ảnh chụp màn hình 2024-09-13 213613.png', '2025-05-27');

-- --------------------------------------------------------

--
-- Table structure for table `employee_info`
--

CREATE TABLE `employee_info` (
  `id` int NOT NULL,
  `employee_id` int NOT NULL,
  `firstName` varchar(100) NOT NULL,
  `lastName` varchar(100) NOT NULL,
  `position` varchar(100) NOT NULL,
  `salary` double NOT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `employee_info`
--

INSERT INTO `employee_info` (`id`, `employee_id`, `firstName`, `lastName`, `position`, `salary`, `date`) VALUES
(5, 1, 'Hoàng', 'Nghĩa', 'Marketer Coordinator', 5000, '2025-05-24'),
(6, 2, 'Trung', 'Đỗ', 'Web Developer(Back End)', 4000, '2025-05-24'),
(7, 3, 'Eddy', 'Hoang', 'Web Developer(Font End)', 1000, '2025-05-24'),
(8, 4, 'Nguyễn', 'Điền', 'App Developer', 2500, '2025-05-24'),
(9, 5, 'Hồ ', 'Huy', 'Marketer Coordinator', 50000, '2025-05-24'),
(10, 5, 'Hồ', 'Huy', 'Web Developer(Font End)', 50000, '2025-05-25'),
(11, 5, 'Cường', 'test', 'Web Developer(Back End)', 50000, '2025-05-26'),
(12, 5, 'Hồ', 'Huy', 'Web Developer(Back End)', 50000, '2025-05-27'),
(13, 6, 'Tết', 'tét', 'Web Developer(Back End)', 0, '2025-05-27'),
(14, 7, 'Test', 'test', 'Marketer Coordinator', 0, '2025-05-27');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_info`
--
ALTER TABLE `employee_info`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `employee`
--
ALTER TABLE `employee`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT for table `employee_info`
--
ALTER TABLE `employee_info`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
