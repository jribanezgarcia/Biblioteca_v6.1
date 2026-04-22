-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: biblioteca
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audiolibro`
--

DROP TABLE IF EXISTS `audiolibro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audiolibro` (
  `isbn` varchar(13) COLLATE utf8mb4_spanish_ci NOT NULL,
  `duracion_segundos` int unsigned NOT NULL,
  `formato` varchar(30) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`isbn`),
  CONSTRAINT `fk_audiolibro_libro` FOREIGN KEY (`isbn`) REFERENCES `libro` (`isbn`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audiolibro`
--

LOCK TABLES `audiolibro` WRITE;
/*!40000 ALTER TABLE `audiolibro` DISABLE KEYS */;
INSERT INTO `audiolibro` VALUES ('9783161484100',1216140,'mp4b'),('9783161484111',12000,'mp4'),('9783161484201',12000,'mp3'),('9788437604999',1000,'mp4');
/*!40000 ALTER TABLE `audiolibro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `autor`
--

DROP TABLE IF EXISTS `autor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `autor` (
  `idAutor` int unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellidos` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `nacionalidad` varchar(60) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`idAutor`),
  UNIQUE KEY `uk_autor_identidad` (`nombre`,`apellidos`,`nacionalidad`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autor`
--

LOCK TABLES `autor` WRITE;
/*!40000 ALTER TABLE `autor` DISABLE KEYS */;
INSERT INTO `autor` VALUES (51,'HOMERO','SIMPSON','çEstadounidense'),(47,'jander','clander','Española'),(74,'Juan Jose','Vera','Española'),(52,'Lamine','Yamal','Marroqui'),(71,'Miguel ','De Cervantes','Castellana'),(67,'Paco','Pil','Zamorana'),(68,'Paco1','Pil1','Zamorana1'),(69,'Paco2','Pil2','Zamorana2');
/*!40000 ALTER TABLE `autor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `direccion`
--

DROP TABLE IF EXISTS `direccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `direccion` (
  `dni` char(9) COLLATE utf8mb4_spanish_ci NOT NULL,
  `via` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `numero` varchar(10) COLLATE utf8mb4_spanish_ci NOT NULL,
  `cp` char(5) COLLATE utf8mb4_spanish_ci NOT NULL,
  `localidad` varchar(60) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`dni`),
  CONSTRAINT `fk_direccion_usuario` FOREIGN KEY (`dni`) REFERENCES `usuario` (`dni`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `direccion`
--

LOCK TABLES `direccion` WRITE;
/*!40000 ALTER TABLE `direccion` DISABLE KEYS */;
INSERT INTO `direccion` VALUES ('12345678A','Calle','1','28080','Madrid'),('75238366A','Calle Arbol de la seda','120','04009','Almeria'),('75238369X','calle altamira','12','04006','almeria'),('75238370B','Altamira','13','04005','Almeria'),('75238388Z','sdsa','dsadas','04006','sadsa'),('75238866Y','Calle Altamira','1','28080','Madrid');
/*!40000 ALTER TABLE `direccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro`
--

DROP TABLE IF EXISTS `libro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro` (
  `isbn` varchar(13) COLLATE utf8mb4_spanish_ci NOT NULL,
  `titulo` varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
  `anio` int NOT NULL,
  `categoria` enum('NOVELA','ENSAYO','INFANTIL','COMIC','POESIA','TECNICO','OTROS') COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro`
--

LOCK TABLES `libro` WRITE;
/*!40000 ALTER TABLE `libro` DISABLE KEYS */;
INSERT INTO `libro` VALUES ('9783161484100','LA ODISEA',199,'COMIC'),('9783161484111','Linea de Cal',2000,'NOVELA'),('9783161484200','El Quijote',1,'POESIA'),('9783161484201','Programacion',1999,'TECNICO'),('9788437604999','La Biblia',1,'OTROS');
/*!40000 ALTER TABLE `libro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libro_autor`
--

DROP TABLE IF EXISTS `libro_autor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `libro_autor` (
  `isbn` varchar(13) COLLATE utf8mb4_spanish_ci NOT NULL,
  `idAutor` int unsigned NOT NULL,
  PRIMARY KEY (`isbn`,`idAutor`),
  KEY `fk_libroautor_autor` (`idAutor`),
  CONSTRAINT `fk_libroautor_autor` FOREIGN KEY (`idAutor`) REFERENCES `autor` (`idAutor`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_libroautor_libro` FOREIGN KEY (`isbn`) REFERENCES `libro` (`isbn`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libro_autor`
--

LOCK TABLES `libro_autor` WRITE;
/*!40000 ALTER TABLE `libro_autor` DISABLE KEYS */;
INSERT INTO `libro_autor` VALUES ('9783161484111',47),('9783161484100',51),('9783161484100',52),('9788437604999',67),('9788437604999',68),('9788437604999',69),('9783161484200',71),('9783161484201',74);
/*!40000 ALTER TABLE `libro_autor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prestamo`
--

DROP TABLE IF EXISTS `prestamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prestamo` (
  `dni` char(9) COLLATE utf8mb4_spanish_ci NOT NULL,
  `isbn` varchar(13) COLLATE utf8mb4_spanish_ci NOT NULL,
  `fInicio` date NOT NULL,
  `fLimite` date NOT NULL,
  `devuelto` tinyint(1) NOT NULL DEFAULT '0',
  `fDevolucion` date DEFAULT NULL,
  PRIMARY KEY (`dni`,`isbn`,`fInicio`),
  KEY `fk_prestamo_libro` (`isbn`),
  CONSTRAINT `fk_prestamo_libro` FOREIGN KEY (`isbn`) REFERENCES `libro` (`isbn`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_prestamo_usuario` FOREIGN KEY (`dni`) REFERENCES `usuario` (`dni`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prestamo`
--

LOCK TABLES `prestamo` WRITE;
/*!40000 ALTER TABLE `prestamo` DISABLE KEYS */;
INSERT INTO `prestamo` VALUES ('12345678A','9783161484201','2026-04-15','2026-04-30',1,'2026-04-15'),('75238366A','9783161484200','2026-04-12','2026-04-27',1,'2026-04-12'),('75238369X','9788437604999','2026-04-08','2026-04-23',1,'2026-04-12'),('75238369X','9788437604999','2026-04-12','2026-04-27',0,NULL),('75238370B','9783161484200','2026-04-12','2026-04-27',0,NULL);
/*!40000 ALTER TABLE `prestamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `dni` char(9) COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre` varchar(60) COLLATE utf8mb4_spanish_ci NOT NULL,
  `email` varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
  PRIMARY KEY (`dni`),
  UNIQUE KEY `uk_usuario_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES ('12345678A','prueba1','prueba1@gmail.com'),('75238366A','cristifit','cristifit@gmail.com'),('75238369X','rafit','sdsa@gmail.com'),('75238370B','antofit','antoiba@gmail.com'),('75238388Z','dssasda','sadsadas@gmail.com'),('75238866Y','pacojemez','pacojemez@whestham.com');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-15 21:09:53
