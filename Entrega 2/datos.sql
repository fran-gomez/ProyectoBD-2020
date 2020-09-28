USE parquimetros;

/* TODO:
        Agregar los tipos de tarjeta 
        Mejorar los tiempos en los campos
*/

/* Insercion de conductores */
INSERT INTO Conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (12345678,'Philip J.','Fry','Departamento 10100100','87654321',1);

INSERT INTO Conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (23456789,'Leela','Turanga','Alcantarilla','98765432',2);

INSERT INTO Conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (34567890,'Bender B.','Rodriguez','Departamento 10100100','09876543',3);

INSERT INTO Conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (45678901,'Amy','Wong','Rancho Marte','10987654',4);

INSERT INTO Conductores(dni,nombre,apellido,direccion,telefono,registro)
VALUES (56789012,'Hubert','Farnsworth','West 57th street','21098765',5);

/* Insercion de automoviles */
INSERT INTO Automoviles(patente,marca,modelo,color,dni)
VALUES ('ABC123','Dodge','Challenger','Rojo',34567890);

INSERT INTO Automoviles(patente,marca,modelo,color,dni)
VALUES ('FALEN1','Chevrolet','Corvette','Negro',12345678);

INSERT INTO Automoviles(patente,marca,modelo,color,dni)
VALUES ('BSD114','Canyonero','F','Rojo',56789012);

INSERT INTO Automoviles(patente,marca,modelo,color,dni)
VALUES ('BURNS1','Lamborghinni','Fasterossa','Blanco',56789012);

INSERT INTO Automoviles(patente,marca,modelo,color,dni)
VALUES ('DOOP-1','Nimbus','-','Blanco',56789012);

/* Insercion de tarjetas */
INSERT INTO Tarjeta(id_tarjeta,saldo,tipo,patente)
VALUES(1,0.23,'Basica','ABC123');

INSERT INTO Tarjeta(id_tarjeta,saldo,tipo,patente)
VALUES(2,999.99,'Premium','FALEN1');

INSERT INTO Tarjeta(id_tarjeta,saldo,tipo,patente)
VALUES(3,999.99,'Premium','FALEN1');

INSERT INTO Tarjeta(id_tarjeta,saldo,tipo,patente)
VALUES(4,35.78,'Basica','BSD114');

INSERT INTO Tarjeta(id_tarjeta,saldo,tipo,patente)
VALUES(5,2.00,'Premium','BURNS1');

/* Insercion de inspectores */
INSERT INTO Inspectores(legajo,dni,nombre,apellido,password)
VALUES (5154,67890123,'Hermes','Conrad',md5('burocrata rango 35'));

INSERT INTO Inspectores(legajo,dni,nombre,apellido,password)
VALUES (123,78901234,'Zapp','Branigan',md5('amante estelar'));

INSERT INTO Inspectores(legajo,dni,nombre,apellido,password)
VALUES (978684,89012345,'Kiff','Crockett',md5('amy wong <3'));

/* Insercion de ubicaciones */
INSERT INTO Ubicaciones(calle,altura,tarifa)
VALUES ('Calle falsa','123',23.457);

INSERT INTO Ubicaciones(calle,altura,tarifa)
VALUES ('43rd street','450W',66.75);

INSERT INTO Ubicaciones(calle,altura,tarifa)
VALUES ('Siempre Viva','742',2.38);

/* Insercion de parquimetros */
INSERT INTO Parquimetros(id_parq,numero,calle,altura)
VALUES (1,16,'Calle falsa','123');

INSERT INTO Parquimetros(id_parq,numero,calle,altura)
VALUES (2,17,'Siempre Viva','742');

INSERT INTO Parquimetros(id_parq,numero,calle,altura)
VALUES (3,18,'43rd street','450W');

/* Insercion de Estacionamientos */
INSERT INTO Estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (3,1,CURTIME(),CURDATE(),NULL,NULL);

INSERT INTO Estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (5,3,CURTIME(),CURDATE(),CURTIME(),CURDATE());

INSERT INTO Estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (1,2,CURTIME(),CURDATE(),NULL,NULL);
/*
INSERT INTO Estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent,fecha_sal,hora_sal)
VALUES (2,1,CURTIME(),CURDATE(),CURTIME(),CURDATE());*/

/* Insercion en accede */
INSERT INTO Accede(legajo,id_parq,fecha,hora)
VALUES (5154,1,CURDATE(),CURTIME());

INSERT INTO Accede(legajo,id_parq,fecha,hora)
VALUES (123,2,CURDATE(),CURTIME());

INSERT INTO Accede(legajo,id_parq,fecha,hora)
VALUES (978684,3,CURDATE(),CURTIME());

/* Insercion en asociado_con */
INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (1,5154,'Siempre Viva','742','lu','M');

INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (2,5154,'Calle falsa','123','vi','T');

INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (3,978684,'43rd street','450W','ju','M');

INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (4,123,'Siempre Viva','742','ma','M');

INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (5,978684,'Siempre Viva','742','mi','T');

INSERT INTO Asociado_con(id_asociado_con,legajo,calle,altura,dia,turno)
VALUES (6,978684,'43rd street','450W','do','T');

/* Insercion en multas */
INSERT INTO Multas(numero,fecha,hora,patente,id_asociado_con)
VALUES (1,'1999-12-31','23:59:30','FALEN1',3);

INSERT INTO Multas(numero,fecha,hora,patente,id_asociado_con)
VALUES (2,'2099-12-31','23:59:59','FALEN1',6);

INSERT INTO Multas(numero,fecha,hora,patente,id_asociado_con)
VALUES (3,'3001-6-13','16:45:00','BSD114',4);