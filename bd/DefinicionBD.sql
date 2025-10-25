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

CREATE TABLE Redes ( 
	redId INT NOT NULL,
	redNombre VARCHAR( 20 ) NOT NULL
)
ALTER TABLE Redes 
	ADD CONSTRAINT PK_Red PRIMARY KEY( redId )
GO

CREATE TABLE Paises ( 
	paisId INT NOT NULL,
	paisNombre VARCHAR( 25 ) NOT NULL
)
ALTER TABLE Paises 
	ADD CONSTRAINT PK_Pais PRIMARY KEY( paisId )
GO

CREATE TABLE Solicitudes (
	solIdTarjeta INT NULL,
	solFecha DATE NOT NULL,
	solColocada CHAR(2) NOT NULL, --'Y'/'N'
	solRedId INT NOT NULL,
	solPaisId INT NOT NULL,
	solClId INT NOT NULL,
	solEmpId INT NOT NULL,
	solObjetivo INT NOT NULL
)

CREATE CLUSTERED INDEX IX_Sol_Fecha_Emp ON Solicitudes( solFecha, solEmpId ) 
CREATE NONCLUSTERED INDEX IX_Sol_Tarjeta ON Solicitudes( solIdTarjeta ) 
CREATE NONCLUSTERED INDEX IX_Sol_Emp ON Solicitudes( solEmpId ) 
CREATE NONCLUSTERED INDEX IX_Sol_Cl ON Solicitudes( solClId )
CREATE NONCLUSTERED INDEX IX_Sol_Pais ON Solicitudes( solPaisId )
CREATE NONCLUSTERED INDEX IX_Sol_Red ON Solicitudes( solRedId )
GO

CREATE TABLE Transacciones (
	tranIdTarjeta INT NOT NULL,
	tranFecha DATE NOT NULL,
	tranPaisId INT NOT NULL,
	tranImporte NUMERIC( 6, 2 ) NOT NULL
)

CREATE CLUSTERED INDEX IX_Tran_Fecha ON Transacciones( tranFecha ) 
CREATE NONCLUSTERED INDEX IX_Tran_Tarjeta ON Transacciones( tranIdTarjeta ) 
CREATE NONCLUSTERED INDEX IX_Tran_Pais ON Transacciones( tranPaisId )
GO

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