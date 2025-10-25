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

ALTER TABLE Paises 
	ADD CONSTRAINT FK_Paises FOREIGN KEY( paisNombre ) REFERENCES dimensionPaises( paisNombre )
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
	numDia TINYINT NOT NULL,
	nomDia VARCHAR(20) NOT NULL,
	nomMes VARCHAR(10) NOT NULL,
	año SMALLINT NOT NULL,
	bimestre TINYINT NOT NULL,
	trimestre TINYINT NOT NULL,
	cuatrimestre TINYINT NOT NULL,
	semestre TINYINT NOT NULL,
	semanaAño TINYINT NOT NULL,
	diaAño SMALLINT NOT NULL,
	quincenaAño TINYINT NOT NULL,
	quincenaMes TINYINT NOT NULL,
	semanaMes TINYINT NOT NULL,

	esBisiesto CHAR(1) NOT NULL, --Y/'N'
	esFestivo CHAR(1) NOT NULL,
	esFestivoLaboral CHAR(1) NOT NULL,
	esLaboral CHAR(1) NOT NULL,
	esQuincena CHAR(1) NOT NULL,
	
	festejo VARCHAR( 50) NULL,
	estacion VARCHAR( 20) NOT NULL,
	signoZodiacal VARCHAR( 20) NOT NULL,
	animalChino VARCHAR( 20) NOT NULL,
	elementoChino VARCHAR( 20) NOT NULL
)

ALTER TABLE dimensionTiempo 
ADD 
	CONSTRAINT PK_Tiempo PRIMARY KEY( fecha )
GO

/*
--DIMENSION OBJETIVOS:
CREATE DROP TABLE Objetivos (
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
*/