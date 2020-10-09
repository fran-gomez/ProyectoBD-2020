CREATE DATABASE parquimetros;
USE parquimetros;

# Entidades

CREATE TABLE conductores (
    dni INT UNSIGNED NOT NULL, 
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30),
    registro INT UNSIGNED NOT NULL,

    CONSTRAINT pk_conductores
    PRIMARY KEY (dni)
) ENGINE = InnoDB;

CREATE TABLE automoviles (
    patente CHAR(6) NOT NULL,
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    dni INT UNSIGNED NOT NULL,

    CONSTRAINT pk_automoviles
    PRIMARY KEY (patente),

    CONSTRAINT pk_automoviles_conductores
    FOREIGN KEY (dni) REFERENCES conductores(dni) 
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE tipos_tarjeta (
    tipo VARCHAR(30),
    descuento DECIMAL(3,2) UNSIGNED NOT NULL,

    CONSTRAINT pk_tipos_tarjeta
    PRIMARY KEY (tipo)
) ENGINE = InnoDB;

CREATE TABLE tarjetas (
    id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    saldo DECIMAL(5,2) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    patente CHAR(6) NOT NULL,

    CONSTRAINT pk_tarjeta
    PRIMARY KEY (id_tarjeta),

    CONSTRAINT fk_tarjeta_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_tarjeta_tipos_tarjeta
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;


CREATE TABLE inspectores (
    legajo INT UNSIGNED NOT NULL,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    password VARCHAR(32) NOT NULL,

    CONSTRAINT pk_inspectores
    PRIMARY KEY (legajo)
) ENGINE = InnoDB;

CREATE TABLE ubicaciones (
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL, 
    tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

    CONSTRAINT pk_ubicaciones
    PRIMARY KEY (calle,altura)
) ENGINE = InnoDB;

CREATE TABLE parquimetros (
    id_parq INT UNSIGNED NOT NULL,
    numero INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,

    CONSTRAINT pk_parquimetros
    PRIMARY KEY (id_parq),

    CONSTRAINT fk_parquimetros_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

# Relaciones 

CREATE TABLE estacionamientos (
    id_tarjeta INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha_ent DATE NOT NULL,
    hora_ent TIME NOT NULL,
    fecha_sal DATE,
    hora_sal TIME,

    CONSTRAINT pk_estacionamientos
    PRIMARY KEY (id_parq,fecha_ent,hora_ent),

    CONSTRAINT fk_estacionamientos_tarjetas
    FOREIGN KEY (id_tarjeta) 
    REFERENCES tarjetas(id_tarjeta)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_estacionamientos_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE accede (
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_accede
    PRIMARY KEY (id_parq,fecha,hora),

    CONSTRAINT fk_accede_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_accede_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE asociado_con (
    id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    dia ENUM('do', 'lu', 'ma', 'mi', 'ju', 'vi', 'sa') NOT NULL,
    turno ENUM('M', 'T') NOT NULL,

    CONSTRAINT pk_asociado_con
    PRIMARY KEY (id_asociado_con),

    CONSTRAINT fk_asociado_con_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_asociado_con_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE multa (
    numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    patente CHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,

    CONSTRAINT pk_multa
    PRIMARY KEY (numero),

    CONSTRAINT fk_multa_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_multa_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

# Tareas administrativas

# El usuario admin es un usuario local, con permisos
# totales sobre la base de datos 

DROP USER admin@localhost;
FLUSH PRIVILEGES;
CREATE USER admin@localhost IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON parquimetros.* TO admin@localhost;

# El usuario venta es un usuario remoto que solo tiene
# permiso para agregar tarjetas nuevas (que haya vendido)
# a la base de datos

DROP USER venta@'%';
FLUSH PRIVILEGES;
CREATE USER venta@'%' IDENTIFIED BY 'venta';
GRANT INSERT ON parquimetros.tarjetas TO venta@'%';

# Crea la vista de estacionados

CREATE VIEW estacionados AS 
    SELECT calle, altura, patente
    FROM (parquimetros.parquimetros NATURAL JOIN estacionamientos NATURAL JOIN tarjetas)
    WHERE fecha_sal = NULL and hora_sal = NULL;

# El usuario inspector es un usuario remoto que puede 
# validar el numero y legajo de otro inspector, cargar
# multa, registrar el acceso a los parquimetros y 
# consultar las patentes de los autos registrados en 
# un dado parquimetro.
# El view Estacionados se encarga de modelar los datos
# para la ultima funcionalidad del inspector.

DROP USER inspector@'%';
FLUSH PRIVILEGES;
CREATE USER inspector@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.inspectores TO inspector@'%';
GRANT SELECT, UPDATE, INSERT ON parquimetros.parquimetros TO inspector@'%';
GRANT INSERT ON parquimetros.multa TO inspector@'%';
GRANT SELECT ON estacionados TO inspector@'%';