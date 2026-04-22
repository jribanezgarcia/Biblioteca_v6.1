-- =========================================================
--  BIBLIOTECA - Script MySQL (Audiolibro en tabla separada)
--  Herencia: Libro (superclase) / Audiolibro (subclase)
-- =========================================================

DROP SCHEMA IF EXISTS biblioteca;
CREATE SCHEMA IF NOT EXISTS biblioteca
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;
USE biblioteca;

-- (Opcional) usuario BD
DROP USER IF EXISTS 'admin'@'%';
CREATE USER IF NOT EXISTS 'admin'@'%'
  IDENTIFIED BY 'biblioteca-2026';
GRANT ALL PRIVILEGES ON biblioteca.* TO 'admin'@'%';
FLUSH PRIVILEGES;

-- =========================================================
-- TABLA: usuario
-- =========================================================
CREATE TABLE usuario (
  dni    CHAR(9)      NOT NULL,
  nombre VARCHAR(60)  NOT NULL,
  email  VARCHAR(120) NOT NULL,
  PRIMARY KEY (dni),
  UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB;

-- =========================================================
-- TABLA: direccion (1:1 con usuario, PK compartida)
-- =========================================================
CREATE TABLE direccion (
  dni       CHAR(9)      NOT NULL,
  via       VARCHAR(80)  NOT NULL,
  numero    VARCHAR(10)  NOT NULL,
  cp        CHAR(5)      NOT NULL,
  localidad VARCHAR(60)  NOT NULL,
  PRIMARY KEY (dni),
  CONSTRAINT fk_direccion_usuario
    FOREIGN KEY (dni) REFERENCES usuario (dni)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- TABLA: autor
-- =========================================================
CREATE TABLE autor (
  idAutor      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(60)  NOT NULL,
  apellidos    VARCHAR(80)  NOT NULL,
  nacionalidad VARCHAR(60)  NOT NULL,
  PRIMARY KEY (idAutor),
  UNIQUE KEY uk_autor_identidad (nombre, apellidos, nacionalidad)
) ENGINE=InnoDB;

-- =========================================================
-- TABLA: libro (superclase)
-- =========================================================
CREATE TABLE libro (
  isbn      VARCHAR(13) NOT NULL,
  titulo    VARCHAR(120) NOT NULL,
  anio      INT NOT NULL,
  categoria ENUM('NOVELA','ENSAYO','INFANTIL','COMIC','POESIA','TECNICO','OTROS') NOT NULL,
  PRIMARY KEY (isbn)
) ENGINE=InnoDB;

-- =========================================================
-- TABLA: audiolibro (subclase) 1:1 con libro
-- PK = FK a libro(isbn)
-- =========================================================
CREATE TABLE audiolibro (
  isbn              VARCHAR(13)   NOT NULL,
  duracion_segundos INT UNSIGNED  NOT NULL,
  formato           VARCHAR(30)   NOT NULL,
  PRIMARY KEY (isbn),
  CONSTRAINT fk_audiolibro_libro
    FOREIGN KEY (isbn) REFERENCES libro (isbn)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- TABLA intermedia: libro_autor (N:M)
-- (Audiolibro también es Libro, así que comparte la misma relación)
-- =========================================================
CREATE TABLE libro_autor (
  isbn    VARCHAR(13)    NOT NULL,
  idAutor INT UNSIGNED   NOT NULL,
  PRIMARY KEY (isbn, idAutor),
  CONSTRAINT fk_libroautor_libro
    FOREIGN KEY (isbn) REFERENCES libro (isbn)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_libroautor_autor
    FOREIGN KEY (idAutor) REFERENCES autor (idAutor)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- TABLA: prestamo
-- PK compuesta: dni + isbn + fInicio
-- =========================================================
CREATE TABLE prestamo (
  dni         CHAR(9)      NOT NULL,
  isbn        VARCHAR(13)  NOT NULL,
  fInicio     DATE         NOT NULL,
  fLimite     DATE         NOT NULL,
  devuelto    BOOLEAN      NOT NULL DEFAULT FALSE,
  fDevolucion DATE         NULL,

  PRIMARY KEY (dni, isbn, fInicio),

  CONSTRAINT fk_prestamo_usuario
    FOREIGN KEY (dni) REFERENCES usuario (dni)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

  CONSTRAINT fk_prestamo_libro
    FOREIGN KEY (isbn) REFERENCES libro (isbn)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

COMMIT;