-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-12-2024 a las 00:54:59
-- Versión del servidor: 10.4.17-MariaDB
-- Versión de PHP: 8.0.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestion`
--


-- Ahora creamos la base de datos
CREATE DATABASE gestion_FTP;

USE gestion_FTP;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumno`
--




CREATE TABLE `alumno` (
  `Id_Alumno` int(11) NOT NULL,
  `Nombre` varchar(25) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Apellidos` varchar(35) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Tutor_Grupo` int(11) DEFAULT NULL,
  `nombreTutor` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Pendiente` tinyint(1) DEFAULT 0,
  `Curso` varchar(50) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Contacto` varchar(50) COLLATE utf8mb4_spanish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `alumno`
--

INSERT INTO `alumno` (`Id_Alumno`, `Nombre`, `Apellidos`, `Tutor_Grupo`, `nombreTutor`, `Pendiente`, `Curso`, `Contacto`) VALUES
(7, 'rvtb', 'rvtr', 5, '5', 1, 'Segundo_DAM', 'cerv5@gmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumnos_empresas_rel`
--

CREATE TABLE `alumnos_empresas_rel` (
  `Id_Asignacion` int(11) NOT NULL,
  `Id_Alumno` int(11) NOT NULL,
  `Nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Apellidos` varchar(90) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Id_Empresa` int(11) NOT NULL,
  `Id_Tutor_Empresa` int(11) DEFAULT NULL,
  `Empresa` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Id_Tutor_Docente` int(11) DEFAULT NULL,
  `Fecha_Inicio` date NOT NULL,
  `Fecha_Fin` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `alumnos_empresas_rel`
--

INSERT INTO `alumnos_empresas_rel` (`Id_Asignacion`, `Id_Alumno`, `Nombre`, `Apellidos`, `Id_Empresa`, `Id_Tutor_Empresa`, `Empresa`, `Id_Tutor_Docente`, `Fecha_Inicio`, `Fecha_Fin`) VALUES
(1, 7, 'rvtb', 'rvtr', 1, 2, 'rcrccrcr', 5, '2024-10-30', '2024-11-23');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comentarios_empresa`
--

CREATE TABLE `comentarios_empresa` (
  `Id_Comentario` int(11) NOT NULL,
  `Comentario` text COLLATE utf8mb4_spanish_ci NOT NULL,
  `Fecha_Comentario` timestamp NOT NULL DEFAULT current_timestamp(),
  `Id_Empresa` int(11) NOT NULL,
  `nombreEmpresa` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `comentarios_empresa`
--

INSERT INTO `comentarios_empresa` (`Id_Comentario`, `Comentario`, `Fecha_Comentario`, `Id_Empresa`, `nombreEmpresa`) VALUES
(1, ' njkm', '2024-11-07 00:00:00', 1, 'rcrccrcr');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresas`
--

CREATE TABLE `empresas` (
  `Id_Empresa` int(11) NOT NULL,
  `Nombre` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Direccion` text COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Telefono` varchar(15) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Correo` varchar(100) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Participa` tinyint(1) DEFAULT 0,
  `Plazas` int(11) DEFAULT NULL,
  `Especialidad` varchar(50) COLLATE utf8mb4_spanish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `empresas`
--

INSERT INTO `empresas` (`Id_Empresa`, `Nombre`, `Direccion`, `Telefono`, `Correo`, `Participa`, `Plazas`, `Especialidad`) VALUES
(1, 'rcrccrcr', 'ccr', '9161662622', 'ccrccr@gmail.com', 1, 1, 'cerc');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tutor_empresa`
--

CREATE TABLE `tutor_empresa` (
  `Id_Tutor` int(11) NOT NULL,
  `Nombre` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Contacto` varchar(50) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Id_Empresa` int(10) NOT NULL,
  `nombreEmpresa` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `tutor_empresa`
--

INSERT INTO `tutor_empresa` (`Id_Tutor`, `Nombre`, `Contacto`, `Id_Empresa`, `nombreEmpresa`) VALUES
(2, 'h gf', 'gbhnj@bnjmk.com', 1, 'rcrccrcr');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tutor_grupo`
--

CREATE TABLE `tutor_grupo` (
  `Id_Tutor` int(11) NOT NULL,
  `Nombre` varchar(35) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Grupo` varchar(32) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `tutor_grupo`
--

INSERT INTO `tutor_grupo` (`Id_Tutor`, `Nombre`, `Grupo`) VALUES
(5, 'tvrc', 'Segundo_DAM');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `visitas`
--

CREATE TABLE `visitas` (
  `Id_Visita` int(11) NOT NULL,
  `Fecha_Visita` date NOT NULL,
  `Observaciones` text COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `Id_Alumno` int(11) NOT NULL,
  `nombreAlumno` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellidosAlumno` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  `Id_Tutor` int(11) NOT NULL,
  `nombreTutorGrupo` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `visitas`
--

INSERT INTO `visitas` (`Id_Visita`, `Fecha_Visita`, `Observaciones`, `Id_Alumno`, `nombreAlumno`, `apellidosAlumno`, `Id_Tutor`, `nombreTutorGrupo`) VALUES
(1, '2024-11-07', 'cg', 7, 'tvrc', 'rvtr', 5, 'rvtb');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `alumno`
--
ALTER TABLE `alumno`
  ADD PRIMARY KEY (`Id_Alumno`),
  ADD KEY `Fk_Tutor_Grupo` (`Tutor_Grupo`);

--
-- Indices de la tabla `alumnos_empresas_rel`
--
ALTER TABLE `alumnos_empresas_rel`
  ADD PRIMARY KEY (`Id_Asignacion`),
  ADD KEY `Id_Alumno` (`Id_Alumno`),
  ADD KEY `Id_Empresa` (`Id_Empresa`),
  ADD KEY `Id_Tutor_Empresa` (`Id_Tutor_Empresa`),
  ADD KEY `Id_Tutor_Docente` (`Id_Tutor_Docente`);

--
-- Indices de la tabla `comentarios_empresa`
--
ALTER TABLE `comentarios_empresa`
  ADD PRIMARY KEY (`Id_Comentario`),
  ADD KEY `Id_Empresa` (`Id_Empresa`);

--
-- Indices de la tabla `empresas`
--
ALTER TABLE `empresas`
  ADD PRIMARY KEY (`Id_Empresa`);

--
-- Indices de la tabla `tutor_empresa`
--
ALTER TABLE `tutor_empresa`
  ADD PRIMARY KEY (`Id_Tutor`);

--
-- Indices de la tabla `tutor_grupo`
--
ALTER TABLE `tutor_grupo`
  ADD PRIMARY KEY (`Id_Tutor`);

--
-- Indices de la tabla `visitas`
--
ALTER TABLE `visitas`
  ADD PRIMARY KEY (`Id_Visita`),
  ADD KEY `Id_Alumno` (`Id_Alumno`),
  ADD KEY `Id_Tutor` (`Id_Tutor`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `alumno`
--
ALTER TABLE `alumno`
  MODIFY `Id_Alumno` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `alumnos_empresas_rel`
--
ALTER TABLE `alumnos_empresas_rel`
  MODIFY `Id_Asignacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `comentarios_empresa`
--
ALTER TABLE `comentarios_empresa`
  MODIFY `Id_Comentario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `empresas`
--
ALTER TABLE `empresas`
  MODIFY `Id_Empresa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `tutor_empresa`
--
ALTER TABLE `tutor_empresa`
  MODIFY `Id_Tutor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `tutor_grupo`
--
ALTER TABLE `tutor_grupo`
  MODIFY `Id_Tutor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `visitas`
--
ALTER TABLE `visitas`
  MODIFY `Id_Visita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `alumno`
--
ALTER TABLE `alumno`
  ADD CONSTRAINT `Fk_Tutor_Grupo` FOREIGN KEY (`Tutor_Grupo`) REFERENCES `tutor_grupo` (`Id_Tutor`);

--
-- Filtros para la tabla `alumnos_empresas_rel`
--
ALTER TABLE `alumnos_empresas_rel`
  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_1` FOREIGN KEY (`Id_Alumno`) REFERENCES `alumno` (`Id_Alumno`) ON DELETE CASCADE,
  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_2` FOREIGN KEY (`Id_Empresa`) REFERENCES `empresas` (`Id_Empresa`) ON DELETE CASCADE,
  ADD CONSTRAINT `alumnos_empresas_rel_ibfk_4` FOREIGN KEY (`Id_Tutor_Docente`) REFERENCES `tutor_grupo` (`Id_Tutor`) ON DELETE SET NULL;

--
-- Filtros para la tabla `comentarios_empresa`
--
ALTER TABLE `comentarios_empresa`
  ADD CONSTRAINT `comentarios_empresa_ibfk_1` FOREIGN KEY (`Id_Empresa`) REFERENCES `empresas` (`Id_Empresa`) ON DELETE CASCADE;

--
-- Filtros para la tabla `visitas`
--
ALTER TABLE `visitas`
  ADD CONSTRAINT `visitas_ibfk_1` FOREIGN KEY (`Id_Alumno`) REFERENCES `alumno` (`Id_Alumno`) ON DELETE CASCADE,
  ADD CONSTRAINT `visitas_ibfk_2` FOREIGN KEY (`Id_Tutor`) REFERENCES `tutor_grupo` (`Id_Tutor`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
