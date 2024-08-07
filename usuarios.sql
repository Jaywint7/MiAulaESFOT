-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: bwhrnrxq2kqlsgfno7nj-mysql.services.clever-cloud.com:3306
-- Tiempo de generación: 07-08-2024 a las 01:06:01
-- Versión del servidor: 8.0.22-13
-- Versión de PHP: 8.2.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- Base de datos: `bwhrnrxq2kqlsgfno7nj`

-- --------------------------------------------------------
--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contraseña` varchar(100) NOT NULL,
  `tipo_usuario` enum('Administrador','Profesor','Estudiante') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `apellido`, `email`, `contraseña`, `tipo_usuario`) VALUES
(1, 'Admin', 'User', 'admin@esfot.edu.ec', '$2a$10$luKcdPNJFteK8y5LAcc4W.GZy3F2TNHDtTcRkyGLRLQkfABfiM5Sa', 'Administrador'),
(2, 'Juan', 'Perez', 'juan@esfot.edu.ec', '$2a$10$KqNOkmWZPHHTeHt8mLneQ.EdnuGkvHgosmka9mb6hHYLg1t8ldS7m', 'Profesor'),
(3, 'Jair', 'Vega', 'jair@esfot.edu.ec', '$2a$10$uNwE.NG4n3XhEaF/ILda9upsU/JWCuOjIM6vDkyT.1a4Rr6DTOjiC', 'Estudiante'),
(6, 'Nico', 'Williams', 'nico@esfot.edu.ec', '$2a$10$fVnRmsN8DLfZi4X8Ew1mHuBZxjSpKd32P4IhVjMki4fi7YkC6E376', 'Profesor'),
(8, 'Luis', 'Diaz', 'luis@esfot.edu.ec', '$2a$10$FZTsd9CNYmuCWxkHzKa.weSSR0n2K02OsA9imxwN2BzTJ87KEO23S', 'Administrador'),
(9, 'Leo', 'Mernuel', 'leo@esfot.edu.ec', '$2a$10$UIl9yRmjAxkBKfmzOy9e8.wE0NBZfwbkVB4Ge7GE7bUUSCMYMZ9zy', 'Estudiante');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
