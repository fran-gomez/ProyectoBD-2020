CREATE DATABASE parquimetros;
USE parquimetros;

/* TODO:
        Restricciones de campo en casi todas las tablas
 */

CREATE TABLE Conductores (
    dni INT UNSIGNED,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    registro INT UNSIGNED NOT NULL,

    PRIMARY KEY (dni)
);

CREATE TABLE Automoviles (
    patente CHAR(6),
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    dni INT UNSIGNED,

    PRIMARY KEY (patente),
    FOREIGN KEY (dni) REFERENCES Conductores(dni)
);

CREATE TABLE Tipos_tarjeta (
    tipo VARCHAR(30),
    descuento DECIMAL(3,2),

    PRIMARY KEY (tipo)
);

CREATE TABLE Tarjeta (
    id_tarjeta INT UNSIGNED,
    saldo DECIMAL(5,2) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    patente CHAR(6),

    PRIMARY KEY (id_tarjeta),
    FOREIGN KEY (patente) REFERENCES Automoviles(patente)
);

CREATE TABLE Inspectores (
    legajo INT,
    dni INT,
    nombre VARCHAR(30),
    apellido VARCHAR(30),
    password VARCHAR(32),

    PRIMARY KEY (legajo)
);

CREATE TABLE Ubicaciones (
    calle VARCHAR(30),
    altura VARCHAR(30),
    tarifa DECIMAL(5,2),

    PRIMARY KEY (calle,altura)
);

CREATE TABLE Parquimetros (
    id_parq INT UNSIGNED,
    numero INT UNSIGNED,
    calle VARCHAR(30),
    altura VARCHAR(30),

    PRIMARY KEY (id_parq),
    FOREIGN KEY (calle,altura) REFERENCES Ubicaciones(calle,altura)
);

CREATE TABLE Estacionamientos (
    id_tarjeta INT UNSIGNED,
    id_parq INT UNSIGNED,
    fecha_ent DATE,
    hora_ent TIME,
    fecha_sal DATE,
    hora_sal TIME,

    PRIMARY KEY (id_parq,fecha_ent,hora_ent),
    FOREIGN KEY (id_tarjeta) REFERENCES Tarjeta(id_tarjeta),
    FOREIGN KEY (id_parq) REFERENCES Parquimetros(id_parq)
);

CREATE TABLE Accede (
    legajo INT,
    id_parq INT,
    fecha DATE,
    hora TIME,

    PRIMARY KEY (id_parq,fecha,hora),
    FOREIGN KEY (legajo) REFERENCES Inspectores(legajo)
);

CREATE TABLE Asociado_con (
    id_asociado_con INT,
    legajo INT,
    calle VARCHAR(30),
    altura VARCHAR(30),
    dia ENUM('do', 'lu', 'ma', 'mi', 'ju', 'vi', 'sa'),
    turno ENUM('M', 'T'),

    PRIMARY KEY (id_asociado_con),
    FOREIGN KEY (legajo) REFERENCES Inspectores(legajo),
    FOREIGN KEY (calle,altura) REFERENCES Ubicaciones(calle,altura)
);

CREATE TABLE Multas (
    numero INT UNSIGNED,
    fecha DATE,
    hora TIME,
    patente CHAR(6),
    id_asociado_con INT,

    PRIMARY KEY (numero),
    FOREIGN KEY (patente) REFERENCES Automoviles(patente),
    FOREIGN KEY (id_asociado_con) REFERENCES Asociado_con(id_asociado_con)
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
GRANT INSERT ON parquimetros.Tarjeta TO venta@'%';

/* El usuario inspector es un usuario remoto que puede 
   validarel numero y legajo de otro inspector, cargar
   multas, registrarel acceso a los parquimetros y 
   consultar las patentes de los autos registrados en 
   un dado parquimetro.
   El view Estacionados se encarga de modelar los datos
   para la ultima funcionalidad del inspector. */
DROP USER inspector@'%';
FLUSH PRIVILEGES;
CREATE USER inspector@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.Inspectores TO inspector@'%';
GRANT SELECT, UPDATE, INSERT ON parquimetros.Parquimetros TO inspector@'%';
GRANT INSERT ON parquimetros.Multas TO inspector@'%';

/*CREATE VIEW Estacionados AS Estacionamientos;

GRANT SELECT ON Estacionados TO inspector@'%';*/