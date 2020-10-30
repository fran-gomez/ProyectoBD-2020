USE parquimetros;

/* Insercion de conductores */
INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (12345678,'Philip J.','Fry','Departamento 10100100','87654321',1);

INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (23456789,'Leela','Turanga','Alcantarilla','98765432',2);

INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (34567890,'Bender B.','Rodriguez','Departamento 10100100','09876543',3);

INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (45678901,'Amy','Wong','Rancho Marte','10987654',4);

INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (56789012,'Hubert','Farnsworth','West 57th street','21098765',5);

/* Insercion de automoviles */
INSERT INTO automoviles(patente,marca,modelo,color,dni)
VALUES ('ABC123','Dodge','Challenger','Rojo',34567890);

INSERT INTO automoviles(patente,marca,modelo,color,dni)
VALUES ('FALEN1','Chevrolet','Corvette','Negro',12345678);

INSERT INTO automoviles(patente,marca,modelo,color,dni)
VALUES ('BSD114','Canyonero','F','Rojo',56789012);

INSERT INTO automoviles(patente,marca,modelo,color,dni)
VALUES ('BURNS1','Lamborghinni','Fasterossa','Blanco',56789012);

INSERT INTO automoviles(patente,marca,modelo,color,dni)
VALUES ('DOOP-1','Nimbus','-','Blanco',56789012);

INSERT INTO tipos_tarjeta(tipo,descuento)
VALUES ('Basica', 0.0);

INSERT INTO tipos_tarjeta(tipo,descuento)
VALUES ('Premium', 0.5);

/* Insercion de tarjetas */
INSERT INTO tarjetas(saldo,tipo,patente)
VALUES(0.23,'Basica','ABC123');

INSERT INTO tarjetas(saldo,tipo,patente)
VALUES(999.99,'Premium','FALEN1');

INSERT INTO tarjetas(saldo,tipo,patente)
VALUES(999.99,'Premium','FALEN1');

INSERT INTO tarjetas(saldo,tipo,patente)
VALUES(35.78,'Basica','BSD114');

INSERT INTO tarjetas(saldo,tipo,patente)
VALUES(2.00,'Premium','BURNS1');

/* Insercion de inspectores */
INSERT INTO inspectores(legajo,dni,nombre,apellido,password)
VALUES (5154,67890123,'Hermes','Conrad',md5('burocrata_rango_35'));

INSERT INTO inspectores(legajo,dni,nombre,apellido,password)
VALUES (123,78901234,'Zapp','Branigan',md5('amante_estelar'));

INSERT INTO inspectores(legajo,dni,nombre,apellido,password)
VALUES (978684,89012345,'Kiff','Crockett',md5('amy_wong_<3'));

/* Insercion de ubicaciones */
INSERT INTO ubicaciones(calle,altura,tarifa)
VALUES ('Calle falsa',123,23.45);

INSERT INTO ubicaciones(calle,altura,tarifa)
VALUES ('43rd street',450,66.75);

INSERT INTO ubicaciones(calle,altura,tarifa)
VALUES ('Siempre Viva',742,2.38);

/* Insercion de parquimetros */
INSERT INTO parquimetros(id_parq,numero,calle,altura)
VALUES (1,16,'Calle falsa',123);

INSERT INTO parquimetros(id_parq,numero,calle,altura)
VALUES (2,17,'Siempre Viva',742);

INSERT INTO parquimetros(id_parq,numero,calle,altura)
VALUES (3,18,'43rd street',450);

/* Insercion de estacionamientos */
INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (3,1,CURTIME(),CURDATE(),NULL,NULL);

INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (5,3,CURTIME(),CURDATE(),CURTIME(),CURDATE());

INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (1,2,CURTIME(),CURDATE(),NULL,NULL);
/*
INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (2,1,CURTIME(),CURDATE(),CURTIME(),CURDATE());*/

/* Insercion en accede */
INSERT INTO accede(legajo,id_parq,fecha,hora)
VALUES (5154,1,CURDATE(),CURTIME());

INSERT INTO accede(legajo,id_parq,fecha,hora)
VALUES (123,2,CURDATE(),CURTIME());

INSERT INTO accede(legajo,id_parq,fecha,hora)
VALUES (978684,3,CURDATE(),CURTIME());

/* Insercion en asociado_con */
INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (1,5154,'Siempre Viva',742,'lu','M');

INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (2,5154,'Calle falsa',123,'vi','T');

INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (3,978684,'43rd street',450,'ju','M');

INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (4,123,'Siempre Viva',742,'ma','M');

INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (5,978684,'Siempre Viva',742,'mi','T');

INSERT INTO asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (6,978684,'43rd street',450,'do','T');

/* Insercion en multas */
INSERT INTO multa(numero,fecha,hora,patente,id_asociado_con)
VALUES (1,'1999-12-31','23:59:30','FALEN1',3);

INSERT INTO multa(numero,fecha,hora,patente,id_asociado_con)
VALUES (2,'2099-12-31','23:59:59','FALEN1',6);

INSERT INTO multa(numero,fecha,hora,patente,id_asociado_con)
VALUES (3,'3001-6-13','16:45:00','BSD114',4);
