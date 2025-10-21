USE MASTER
GO
CREATE DATABASE DWTarjetas
GO
USE DWTarjetas
GO

CREATE TABLE Empleados ( 
	empId INT NOT NULL,
	empNombre VARCHAR( 50 ) NOT NULL,
	empApellidoPat VARCHAR( 20 ) NOT NULL,
	empApellidoMat VARCHAR( 20 ) NOT NULL,
	empGenero CHAR(1) NOT NULL --'M' / 'F'
)
ALTER TABLE Empleados 
	ADD CONSTRAINT PK_Emp PRIMARY KEY( empId )

CREATE NONCLUSTERED INDEX IX_emp_nom1 ON Empleados( empNombre );
CREATE NONCLUSTERED INDEX IX_emp_nom2 ON Empleados( empApellidoMat );
CREATE NONCLUSTERED INDEX IX_emp_nom3 ON Empleados( empApellidoPat );
GO

CREATE TABLE Clientes ( 
	clId INT NOT NULL,
	clNombre VARCHAR( 50 ) NOT NULL,
	clApellidoPat VARCHAR( 20 ) NOT NULL,
	clApellidoMat VARCHAR( 20 ) NOT NULL,
)
ALTER TABLE Clientes 
	ADD CONSTRAINT PK_Cl PRIMARY KEY( clId )

CREATE NONCLUSTERED INDEX IX_cl_nom1 ON Clientes( clNombre );
CREATE NONCLUSTERED INDEX IX_cl_nom2 ON Clientes( clApellidoMat );
CREATE NONCLUSTERED INDEX IX_cl_nom3 ON Clientes( clApellidoPat );
GO

CREATE TABLE Solicitudes (
	solIdTarjeta INT NOT NULL,
	solFecha DATE NOT NULL,
	solColocada CHAR(2) NOT NULL, --'Y'/'N'
	solRed VARCHAR( 20 ) NOT NULL,
	solPais VARCHAR( 20 ) NOT NULL,
	solClId INT NOT NULL,
	solEmpId INT NOT NULL,
	solObjetivo INT NOT NULL
)

CREATE NONCLUSTERED INDEX IX_Sol_Emp ON Solicitudes( solEmpId ) 
CREATE NONCLUSTERED INDEX IX_Sol_Cl ON Solicitudes( solClId )
GO

--TABLA AUXILIAR:
CREATE TABLE ControlIds (
	idInicial INT NOT NULL,
	tipo CHAR(1) NOT NULL,
	idFinal INT NULL
)

ALTER TABLE ControlIds 
ADD 
	CONSTRAINT PK_Nombres PRIMARY KEY ( idInicial, tipo )
GO