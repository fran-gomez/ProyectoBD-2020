CREATE DATABASE parquimetros;
USE parquimetros;

# Entidades

CREATE TABLE Conductores (
    dni INT UNSIGNED NOT NULL, 
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30),
    registro INT UNSIGNED NOT NULL,

    CONSTRAINT pk_conductores
    PRIMARY KEY (dni)
) ENGINE = InnoDB;

CREATE TABLE Automoviles (
    patente CHAR(6) NOT NULL,
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    dni INT UNSIGNED NOT NULL,

    CONSTRAINT pk_automoviles
    PRIMARY KEY (patente),

    CONSTRAINT pk_automoviles_conductores
    FOREIGN KEY (dni) REFERENCES Conductores(dni) 
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
    FOREIGN KEY (patente) REFERENCES Automoviles(patente)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_tarjeta_tipos_tarjeta
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta(tipo)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;


CREATE TABLE Inspectores (
    legajo INT UNSIGNED NOT NULL,
    dni INT UNSIGNED NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    password VARCHAR(32) NOT NULL,

    CONSTRAINT pk_inspectores
    PRIMARY KEY (legajo)
) ENGINE = InnoDB;

CREATE TABLE Ubicaciones (
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL, 
    tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

    CONSTRAINT pk_ubicaciones
    PRIMARY KEY (calle,altura)
) ENGINE = InnoDB;

CREATE TABLE Parquimetros (
    id_parq INT UNSIGNED NOT NULL,
    numero INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,

    CONSTRAINT pk_parquimetros
    PRIMARY KEY (id_parq),

    CONSTRAINT fk_parquimetros_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES Ubicaciones(calle,altura)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE Ventas (
    id_tarjeta INT UNSIGNED NOT NULL,
    tipo_tarjeta VARCHAR(30) NOT NULL,
    saldo DECIMAL(5, 2) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL

    # No le puse claves ni nada similar ya que es una
    # tabla que solo almacena informacion, no se dice
    # nada respecto a los accesos
) ENGINE = InnoDB;

# Relaciones 

CREATE TABLE Estacionamientos (
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
    FOREIGN KEY (id_parq) REFERENCES Parquimetros(id_parq)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE Accede (
    legajo INT UNSIGNED NOT NULL,
    id_parq INT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,

    CONSTRAINT pk_accede
    PRIMARY KEY (id_parq,fecha,hora),

    CONSTRAINT fk_accede_parquimetros
    FOREIGN KEY (id_parq) REFERENCES Parquimetros(id_parq)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_accede_inspectores
    FOREIGN KEY (legajo) REFERENCES Inspectores(legajo)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE Asociado_con (
    id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
    legajo INT UNSIGNED NOT NULL,
    calle VARCHAR(30) NOT NULL,
    altura INT UNSIGNED NOT NULL,
    dia ENUM('do', 'lu', 'ma', 'mi', 'ju', 'vi', 'sa') NOT NULL,
    turno ENUM('M', 'T') NOT NULL,

    CONSTRAINT pk_asociado_con
    PRIMARY KEY (id_asociado_con),

    CONSTRAINT fk_asociado_con_inspectores
    FOREIGN KEY (legajo) REFERENCES Inspectores(legajo)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_asociado_con_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES Ubicaciones(calle,altura)
    ON DELETE cascade ON UPDATE cascade
) ENGINE = InnoDB;

CREATE TABLE Multa (
    numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    patente CHAR(6) NOT NULL,
    id_asociado_con INT UNSIGNED NOT NULL,

    CONSTRAINT pk_multa
    PRIMARY KEY (numero),

    CONSTRAINT fk_multa_automoviles
    FOREIGN KEY (patente) REFERENCES Automoviles(patente)
    ON DELETE cascade ON UPDATE cascade,

    CONSTRAINT fk_multa_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES Asociado_con(id_asociado_con)
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
GRANT SELECT ON parquimetros.tipos_tarjeta TO venta@'%';

# Crea la vista de estacionados

CREATE VIEW Estacionados AS 
    SELECT calle, altura, patente
    FROM (parquimetros.Parquimetros NATURAL JOIN Estacionamientos NATURAL JOIN tarjetas)
    WHERE fecha_sal IS NULL and hora_sal IS NULL;

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
GRANT SELECT ON parquimetros.Inspectores TO inspector@'%';
GRANT SELECT ON parquimetros.Estacionados TO inspector@'%';
GRANT SELECT ON parquimetros.Asociado_con TO inspector@'%';
GRANT SELECT ON parquimetros.Ubicaciones TO inspector@'%';
GRANT SELECT ON parquimetros.Automoviles TO inspector@'%';
GRANT SELECT ON parquimetros.tarjetas TO inspector@'%';
GRANT SELECT ON parquimetros.Estacionamientos TO inspector@'%';
GRANT SELECT, INSERT ON parquimetros.Multa TO inspector@'%';
GRANT SELECT, UPDATE, INSERT ON parquimetros.Parquimetros TO inspector@'%';
GRANT INSERT ON parquimetros.Accede TO inspector@'%';

DELIMITER !
CREATE PROCEDURE conectar(IN id_tarjeta INTEGER, IN id_parquimetro INTEGER)
STORED_P:BEGIN
    DECLARE fecha_salida DATE;
    DECLARE hora_salida TIME;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SELECT 'SQLEXCEPTION!, transacción abortada' AS Operacion;
        ROLLBACK;
    END;

    START TRANSACTION;

    # Se verifica que el id de tarjeta y parquimetro sean validos
    # caso contrario, se produce un error
    IF (NOT EXISTS(SELECT id_parq FROM (Parquimetros) WHERE id_parq=id_parquimetro)) THEN
        SELECT 'Error' AS Operacion, id_parquimetro;
        LEAVE STORED_P;
    END IF;

    IF (NOT EXISTS(SELECT tarjetas.id_tarjeta FROM (tarjetas) WHERE tarjetas.id_tarjeta=id_tarjeta)) THEN
        SELECT 'Error' AS Operacion, id_tarjeta;
        LEAVE STORED_P;
    END IF;

    # Si existe un estacionamiento abierto, se realiza el cierre
    # caso contrario, estamos frente a una apertura
    IF (EXISTS(SELECT * FROM Estacionamientos
            WHERE Estacionamientos.id_tarjeta=id_tarjeta AND fecha_ent IS NOT NULL AND hora_ent IS NOT NULL AND fecha_sal IS NULL AND hora_sal IS NULL)) THEN
        CALL realizar_cierre(id_tarjeta, id_parquimetro);
        LEAVE STORED_P;
    ELSE
        CALL realizar_apertura(id_tarjeta, id_parquimetro);
        LEAVE STORED_P;
    END IF;
    
    COMMIT;
END; !

CREATE PROCEDURE realizar_apertura(IN id_tarjeta INTEGER, IN id_parquimetro INTEGER)
BEGIN
    DECLARE tipo_tarjeta VARCHAR(30);
    DECLARE saldo_actual INTEGER;
    DECLARE tarifa DECIMAL(5, 2);
    DECLARE descuento DECIMAL(3, 2);

    SELECT saldo INTO saldo_actual FROM tarjetas WHERE tarjetas.id_tarjeta=id_tarjeta FOR UPDATE;
    IF saldo_actual < 0 THEN
        SELECT 'Error' AS Operacion, saldo_actual AS Saldo;
    ELSE
        INSERT INTO Estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
        VALUES(id_tarjeta,id_parquimetro,CURDATE(),CURTIME(),NULL,NULL);

        SELECT Ubicaciones.tarifa INTO tarifa FROM (Parquimetros NATURAL JOIN Ubicaciones)
            WHERE id_parq=id_parquimetro;
        SELECT tipos_tarjeta.descuento INTO descuento FROM (tarjetas NATURAL JOIN tipos_tarjeta)
            WHERE tarjetas.id_tarjeta=id_tarjeta;

        SELECT 'Apertura' AS Operacion, saldo_actual/(tarifa*(1-descuento)) AS Tiempo_disponible;
    END IF;
END; !

CREATE PROCEDURE realizar_cierre(IN id_tarjeta INTEGER, IN id_parquimetro INTEGER)
BEGIN
    DECLARE fecha_entrada, fecha_salida DATE;
    DECLARE hora_entrada, hora_salida TIME;
    DECLARE saldo_actual, nuevo_saldo, tiempo INTEGER;
    DECLARE tarifa DECIMAL(5, 2);
    DECLARE descuento DECIMAL(3, 2);

    SELECT saldo INTO saldo_actual FROM tarjetas WHERE tarjetas.id_tarjeta=id_tarjeta FOR UPDATE;
    SELECT Ubicaciones.tarifa INTO tarifa FROM (Estacionamientos NATURAL JOIN Parquimetros NATURAL JOIN Ubicaciones)
        WHERE Estacionamientos.id_tarjeta=id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL;
    SELECT tipos_tarjeta.descuento INTO descuento FROM (tarjetas NATURAL JOIN tipos_tarjeta)
        WHERE tarjetas.id_tarjeta=id_tarjeta;

    SELECT fecha_ent, hora_ent INTO fecha_entrada, hora_entrada FROM (Parquimetros NATURAL JOIN Estacionamientos)
        WHERE Estacionamientos.id_tarjeta=id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL;
    SELECT CURDATE() INTO fecha_salida;
    SELECT CURTIME() INTO hora_salida;
    SELECT TIME_TO_SEC(TIMEDIFF(TIMESTAMP(fecha_salida, hora_salida),
                                TIMESTAMP(fecha_entrada, hora_entrada)))/60 INTO tiempo;

    SET nuevo_saldo = saldo_actual - (tiempo * tarifa * (1-descuento));
    IF (nuevo_saldo < -999.99) THEN
        set nuevo_saldo = -999;
    END IF;
    SELECT nuevo_saldo, saldo_actual, tiempo, tarifa, descuento;
    UPDATE tarjetas SET
        saldo = nuevo_saldo
        WHERE tarjetas.id_tarjeta=id_tarjeta;
    UPDATE Estacionamientos SET
        Estacionamientos.fecha_sal = fecha_salida,
        Estacionamientos.hora_sal = hora_salida
        WHERE Estacionamientos.id_tarjeta=id_tarjeta;
    SELECT 'Cierre' AS Operacion, tiempo AS Duracion, nuevo_saldo AS Saldo;
END; !

CREATE TRIGGER registrar_venta AFTER INSERT ON Estacionamientos
FOR EACH ROW
BEGIN
    DECLARE tipo_tarjeta VARCHAR(30);
    DECLARE saldo_actual INTEGER;

    SELECT saldo INTO saldo_actual FROM tarjetas WHERE tarjetas.id_tarjeta=NEW.id_tarjeta FOR UPDATE;
    SELECT tipo INTO tipo_tarjeta FROM tarjetas WHERE tarjetas.id_tarjeta=NEW.id_tarjeta;
    
    INSERT INTO Ventas(id_tarjeta,tipo_tarjeta,saldo,fecha,hora)
    VALUES(NEW.id_tarjeta,tipo_tarjeta,saldo_actual,CURDATE(),CURTIME());
END; !
DELIMITER ;

DROP USER parquimetro;
FLUSH PRIVILEGES;
CREATE USER parquimetro@'%' IDENTIFIED BY 'parq';
GRANT EXECUTE ON PROCEDURE parquimetros.conectar TO parquimetro;
GRANT SELECT ON Ubicaciones TO parquimetro;
GRANT SELECT ON tarjetas TO parquimetro;
GRANT SELECT ON Parquimetros TO parquimetro;
GRANT SELECT ON tipos_tarjeta TO parquimetro;
GRANT SELECT, INSERT ON Estacionamientos TO parquimetro;
GRANT INSERT ON Ventas to parquimetro;
