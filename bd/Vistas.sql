USE DWTarjetas
GO

CREATE VIEW VW_dimensionTiempo AS
SELECT
    fecha,
    
    'D�a ' + RIGHT('00' + CAST( numDia AS VARCHAR(2)), 2) AS numDia,
    nomDia, nomMes,
    CAST(a�o AS CHAR(4)) AS a�o,
    
    'Bimestre ' + RIGHT('00' + CAST(bimestre AS VARCHAR(2)), 2) AS bimestre,
    'Trimestre ' + RIGHT('00' + CAST(trimestre AS VARCHAR(2)), 2) AS trimestre,
    'Cuatrimestre ' + RIGHT('00' + CAST(cuatrimestre AS VARCHAR(2)), 2) AS cuatrimestre,
    'Semestre ' + RIGHT('00' + CAST(semestre AS VARCHAR(2)), 2) AS semestre,
    'Semana ' + RIGHT('00' + CAST(semanaA�o AS VARCHAR(2)), 2) AS semanaA�o,
    'D�a ' + RIGHT('000' + CAST(diaA�o AS VARCHAR(3)), 3) AS diaA�o,
    'Quincena ' + RIGHT('00' + CAST(quincenaA�o AS VARCHAR(2)), 2) AS quincenaA�o,
    'Quincena ' + RIGHT('00' + CAST(quincenaMes AS VARCHAR(2)), 2) AS quincenaMes,
    
    'Semana ' + RIGHT('00' + CAST( semanaMes AS VARCHAR(2)), 2) AS semanaMes,

    CASE WHEN esBisiesto = 'Y' THEN 'S�' ELSE 'No' END AS esBisiesto,
    CASE WHEN esFestivo = 'Y' THEN 'S�' ELSE 'No' END AS esFestivo,
    CASE WHEN esFestivoLaboral = 'Y' THEN 'S�' ELSE 'No' END AS esFestivoLaboral,
    CASE WHEN esLaboral = 'Y' THEN 'S�' ELSE 'No' END AS esLaboral,
    CASE WHEN esQuincena = 'Y' THEN 'S�' ELSE 'No' END AS esQuincena,
    
    ISNULL(festejo, 'Ninguno') AS festejo,
    estacion,
    signoZodiacal,
    animalChino,
    elementoChino
FROM dimensionTiempo
GO

CREATE VIEW VW_EMPLEADOS AS
SELECT 
    empId,
    empApellidoPat + ' ' + empApellidoMat + ' ' + empNombre AS empNombre,
    empGenero
FROM Empleados
GO

CREATE VIEW VW_CLIENTES AS
SELECT 
    clId,
    clApellidoPat + ' ' + clApellidoMat + ' ' + clNombre AS clNombre
FROM Clientes
GO

--ACTIVIDADES DEL NEGOCIO:
CREATE VIEW VW_IMPORTE_NACIONAL_DOLAR AS

SELECT T.tranPaisId,DP.paisMonedaDolar AS ImporteDolar
FROM
    Transacciones T
JOIN 
    Paises PS ON T.tranPaisId = PS.paisId
JOIN 
    dimensionPaises DP ON PS.paisNombre = DP.paisNombre

GROUP BY T.tranPaisId,DP.paisMonedaDolar
GO

CREATE VIEW VW_HECHOS_TRANSACCIONES AS
SELECT 
    S.solEmpId AS tranEmpIdEmisor,
    S.solRedId AS tranRedId,
    T.tranFecha,
    T.tranPaisId,
    T.tranImporte AS ImporteNacional,
    CAST(T.tranImporte / VIND.ImporteDolar AS NUMERIC(10,2)) AS ImporteDolar
FROM Transacciones AS T
INNER JOIN Solicitudes AS S 
    ON T.tranIdTarjeta = S.solIdTarjeta
INNER JOIN VW_IMPORTE_NACIONAL_DOLAR AS VIND 
    ON VIND.tranPaisId = T.tranPaisId;
GO

--COLOCACIONES
CREATE VIEW VW_Objetivos AS
SELECT 
        s.solFecha AS objFecha,
        s.solEmpId AS objEmpId,
        MAX(s.solObjetivo) AS objMeta,
        COUNT( s.solClId ) AS objClientesAtendidos,
		COUNT( DISTINCT s.solClId ) AS objClientesDistintosAtendidos,
        SUM(
			CASE 
				WHEN s.solColocada = 'Y' 
			THEN 1 ELSE 0 END
		) AS objColocaciones
FROM SOLICITUDES s
    GROUP BY s.solFecha, s.solEmpId
GO

CREATE VIEW VW_HECHOS_COLOCACIONES AS
SELECT 
    s.solFecha,
    s.solEmpId,
	s.solRedId,
	s.solPaisId,
	s.solClId,

    o.*,
    CASE 
        WHEN objColocaciones >= objMeta 
        THEN 1
        ELSE 0
    END AS objMetaAlcanzada, 

    CASE 
        WHEN objColocaciones > objMeta 
        THEN 1
        ELSE 0
    END AS objMetaSuperada, 

    CASE 
        WHEN ( objMeta - objColocaciones ) < 0
        THEN 0
        ELSE objMeta - objColocaciones
    END AS objFaltantes

FROM 
    Solicitudes AS s
    INNER JOIN VW_Objetivos AS o 
        ON s.solEmpId = o.objEmpId 
        AND s.solFecha = o.objFecha
GO

SELECT * FROM VW_HECHOS_COLOCACIONES

SELECT * FROM VW_HECHOS_TRANSACCIONES

SELECT * FROM VW_dimensionTiempo
GO
