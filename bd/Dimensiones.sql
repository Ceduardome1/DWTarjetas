USE DWTarjetas
GO

--DIMENSION PAIS:
CREATE TABLE dimensionPaises (
	paisNombre VARCHAR(25) NOT NULL,
	paisClima VARCHAR(20) NOT NULL,
	paisNivelSeguridad VARCHAR(20) NOT NULL,
	paisNivelEconomico VARCHAR(20) NOT NULL,
	paisGiroEconomico VARCHAR(20) NOT NULL,
	paisIdioma VARCHAR(20) NOT NULL,
	paisMoneda VARCHAR(20) NOT NULL,
	paisContinente VARCHAR(20) NOT NULL
);

ALTER TABLE dimensionPaises 
	ADD CONSTRAINT PK_Paises PRIMARY KEY( paisNombre )
GO

BULK INSERT dimensionPaises
FROM 'D:\Code\Java\TECNM\TESEBADA\DW_TARJETAS\docs\recursos\Paises.csv'
WITH
(
    FIELDTERMINATOR = ',',  -- Separador de campos
    ROWTERMINATOR = '\n',   -- Separador de filas
    FIRSTROW = 2            -- Si el CSV tiene encabezados
);


--DIMENSION TIEMPO:
CREATE TABLE dimensionTiempo (
	fecha DATE NOT NULL,
	dia VARCHAR(9) NOT NULL,
	mes VARCHAR(10) NOT NULL,
	año CHAR(4) NOT NULL,
	bimestre CHAR(2) NOT NULL,
	trimestre CHAR(2) NOT NULL,
	cuatrimestre CHAR(2) NOT NULL,
	semestre CHAR(2) NOT NULL,
	semanaAño CHAR(2) NOT NULL,
	diaAño CHAR(3) NOT NULL,
	quincenaAño CHAR(2) NOT NULL,
	quincenaMes CHAR(2) NOT NULL,

	esBisiesto CHAR(2) NOT NULL,
	esFestivo CHAR(2) NOT NULL,
	esFestivoLaboral CHAR(2) NOT NULL,
	esLaboral CHAR(2) NOT NULL,
	esQuincena CHAR(2) NOT NULL,
	
	festejo VARCHAR( 50) NOT NULL,
	estacion VARCHAR( 20) NOT NULL,
	signoZodiacal VARCHAR( 20) NOT NULL,
	animalChino VARCHAR( 20) NOT NULL,
	elementoChino VARCHAR( 20) NOT NULL
)

ALTER TABLE dimensionTiempo 
ADD 
	CONSTRAINT PK_Tiempo PRIMARY KEY( fecha )
GO

--DIMENSION OBJETIVOS:
CREATE TABLE Objetivos (
	objFecha DATE NOT NULL,
	objEmpId INT NOT NULL,
	objMeta INT NOT NULL,
	objClientesAtendidos INT NOT NULL,
	objClientesDistintosAtendidos INT NOT NULL,
	objColocaciones INT NOT NULL
	
-- CAMPOS CALCULADOS:
--	objMetaAlcanzada 
--	objMetaSuperada
--  objFaltantes
)
ALTER TABLE Objetivos 
	ADD CONSTRAINT PK_Obj PRIMARY KEY( objFecha, objEmpId ),
	CONSTRAINT FK_Obj_Emp FOREIGN KEY( objEmpId ) REFERENCES Empleados( empId )
GO


/*
Tabla de hechos para modelar:
1.- Empleados Que hayan cumplido con la cantidad de tarjetas colocadas en cada fecha.

2.-  Cantidad de tarjetas que le faltaron colocar para cumplir el objetivo por día.

4.- Importes de venta por país

5.-  Importe de venta por nombre empleado. 

NOTA: POR CADA REGISTRO DE SOLICITUD USAR:
	solFecha DATE NOT NULL,
	solRed VARCHAR( 20 ) NOT NULL,
	solPais VARCHAR( 20 ) NOT NULL,
	solEmpId INT NOT NULL,
*/
