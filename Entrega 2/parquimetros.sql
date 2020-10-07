CREATE DATABASE parquimetros;
USE parquimetros;

/* TODO:
        Restricciones de campo en casi todas las tablas
        Crear la vista
 */

CREATE TABLE conductores (
    dni INT UNSIGNED,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30),
    registro INT UNSIGNED NOT NULL,

    PRIMARY KEY (dni)
);

CREATE TABLE automoviles (
    patente CHAR(6),
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    dni INT UNSIGNED NOT NULL,

    PRIMARY KEY (patente),
    FOREIGN KEY (dni) REFERENCES conductores(dni)
);

CREATE TABLE tipos_tarjeta (
    tipo VARCHAR(30),
    descuento DECIMAL(3,2) UNSIGNED NOT NULL,

    PRIMARY KEY (tipo)
);

CREATE TABLE tarjetas (
    id_tarjeta INT UNSIGNED AUTO_INCREMENT,
    saldo DECIMAL(5,2) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    patente CHAR(6) NOT NULL,

    PRIMARY KEY (id_tarjeta),
    FOREIGN KEY (patente) REFERENCES automoviles(patente),
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
);

CREATE TABLE inspectores (
    legajo INT UNSIGNED,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    password VARCHAR(32) NOT NULL,

    PRIMARY KEY (legajo)
);

CREATE TABLE ubicaciones (
    calle VARCHAR(30),
    altura INT UNSIGNED, 
    tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

    PRIMARY KEY (calle,altura)
);

CREATE TABLE parquimetros (
    id_parq INT UNSIGNED,
    numero INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,

    PRIMARY KEY (id_parq),
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
);

CREATE TABLE estacionamientos (
    id_tarjeta INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED,
    fecha_ent DATE,
    hora_ent TIME,
    fecha_sal DATE,
    hora_sal TIME,

    PRIMARY KEY (id_parq,fecha_ent,hora_ent),
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta),
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
);

CREATE TABLE accede (
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE,
    hora TIME,

    PRIMARY KEY (id_parq,fecha,hora),
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq),
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
);

CREATE TABLE asociado_con (
    id_asociado_con INT UNSIGNED AUTO_INCREMENT,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    dia ENUM('do', 'lu', 'ma', 'mi', 'ju', 'vi', 'sa') NOT NULL,
    turno ENUM('M', 'T') NOT NULL,

    PRIMARY KEY (id_asociado_con),
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo),
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
);

CREATE TABLE multa (
    numero INT UNSIGNED AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    patente CHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,

    PRIMARY KEY (numero),
    FOREIGN KEY (patente) REFERENCES automoviles(patente),
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
);

/* El usuario admin es un usuario local, con permisos
   totales sobre la base de datos */
DROP USER admin@localhost;
FLUSH PRIVILEGES;
CREATE USER admin@localhost IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON parquimetros.* TO admin@localhost;

/* El usuario venta es un usuario remoto que solo tiene
   permiso para agregar tarjetas nuevas (que haya vendido)
   a la base de datos */
DROP USER venta@'%';
FLUSH PRIVILEGES;
CREATE USER venta@'%' IDENTIFIED BY 'venta';
GRANT INSERT ON parquimetros.tarjetas TO venta@'%';

/* El usuario inspector es un usuario remoto que puede 
   validarel numero y legajo de otro inspector, cargar
   multa, registrarel acceso a los parquimetros y 
   consultar las patentes de los autos registrados en 
   un dado parquimetro.
   El view Estacionados se encarga de modelar los datos
   para la ultima funcionalidad del inspector. */
DROP USER inspector@'%';
FLUSH PRIVILEGES;
CREATE USER inspector@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.inspectores TO inspector@'%';
GRANT SELECT, UPDATE, INSERT ON parquimetros.parquimetros TO inspector@'%';
GRANT INSERT ON parquimetros.multa TO inspector@'%';

CREATE VIEW Estacionados AS 
    SELECT calle,altura FROM ubicaciones;

GRANT SELECT ON Estacionados TO inspector@'%';