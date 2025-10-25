USE DWTarjetas
GO



--ANALISIS:
SELECT solPaisId,solRedId, solIdTarjeta, ocurrencias = COUNT(*) 
FROM SOLICITUDES 
GROUP BY solPaisId,solRedId, solIdTarjeta
ORDER BY COUNT(*) DESC

SELECT solIdTarjeta, ocurrencias = COUNT(*) 
FROM SOLICITUDES 
GROUP BY solIdTarjeta
ORDER BY COUNT(*) DESC

SELECT *
FROM SOLICITUDES 
WHERE solIdTarjeta = 0 AND solColocada = 'Y'

SELECT *
FROM SOLICITUDES 
WHERE solIdTarjeta > 0 AND solColocada = 'N'

SELECT solPaisId,solRedId, solIdTarjeta, solColocada  
FROM SOLICITUDES 
WHERE solColocada = 'Y'

SELECT * FROM dimensionTiempo

SELECT * FROM dimensionPaises

SELECT * FROM ControlIds

SELECT * FROM CLIENTES 
WHERE clId IN ( 986, 620, 989 )

SELECT * FROM Empleados 
WHERE empId IN ( 985, 542 )

SELECT * FROM Empleados
WHERE empId 
IN (512, 1, 516, 6, 8, 9, 528, 16, 18, 531, 20, 21, 24, 33, 545, 35, 36, 37, 39, 554, 558, 560, 48, 49, 561, 562, 564, 54, 55, 571, 573, 579, 581, 582, 73, 592, 80, 81, 594, 82, 595, 85, 87, 600, 601, 97, 99, 101, 102, 616, 618, 112, 624, 625, 113, 627, 628, 629, 634, 122, 636, 637, 126, 640, 642, 130, 643, 644, 133, 134, 137, 657, 663, 665, 160, 161, 162, 678, 166, 679, 682, 170, 688, 177, 689, 178, 179, 692, 180, 181, 694, 183, 704, 192, 705, 706, 709, 201, 203, 715, 209, 721, 722, 723, 724, 725, 215, 217, 218, 730, 736, 224, 737, 225, 226, 742, 232, 241, 755, 758, 760, 762, 256, 772, 260, 774, 779, 784, 273, 785, 274, 787, 289, 292, 297, 298, 304, 816, 818, 310, 823, 824, 825, 324, 328, 332, 341, 348, 353, 866, 354, 868, 869, 870, 362, 369, 881, 882, 883, 372, 887, 378, 384, 897, 899, 389, 392, 913, 403, 404, 916, 409, 929, 930, 422, 423, 944, 435, 947, 952, 441, 955, 448, 452, 453, 966, 455, 456, 458, 971, 976, 978, 983, 985, 476, 992, 993, 481, 994, 483, 484, 996, 485, 998, 488, 501, 502, 505)

-----
SELECT idFinal, tipo, COUNT(idFinal) 
FROM ControlIds
WHERE idFinal IS NOT NULL AND tipo='E'
GROUP BY idFinal, tipo
ORDER BY tipo, COUNT(idFinal) DESC

--
SELECT * FROM Solicitudes


SELECT solEmpId, COUNT(*) FROM Solicitudes
GROUP BY solEmpId
ORDER BY COUNT(*) DESC

-------

SELECT *
FROM ControlIds
WHERE idFinal IS NOT NULL

SELECT idFinal, COUNT(idFinal)
FROM ControlIds
GROUP BY idFinal
HAVING  COUNT(idFinal) > 1
ORDER BY COUNT(idFinal) DESC

SELECT s.solClId, c.clNombre
FROM Solicitudes s
LEFT JOIN Clientes c ON s.solClId = c.clId
WHERE c.clId IS NULL;

SELECT s.solClId, e.empNombre
FROM Solicitudes s
LEFT JOIN Empleados e ON s.solEmpId = e.empId 
WHERE e.empId IS NULL;

SELECT * FROM Empleados
WHERE empId = 9

SELECT * FROM Objetivos
ORDER BY objColocaciones DESC

SELECT * FROM Objetivos
ORDER BY objEmpId,objFecha ASC

SELECT objEmpId, COUNT(*) FROM Objetivos
GROUP BY objEmpId
ORDER BY COUNT(*) DESC

SELECT solEmpId, solFecha, COUNT(*) FROM Solicitudes
GROUP BY solEmpId, solFecha
ORDER BY COUNT(*) DESC

SELECT * FROM EMPLEADOS
WHERE empId = 145

SELECT empId, empNombre, empApellidoPat, empApellidoMat FROM Empleados
WHERE empId <> 870
AND (
  CASE WHEN empNombre IN ('FEDERICO', 'RAMIREZ', 'GIMENEZ') THEN 1 ELSE 0 END +
  CASE WHEN empApellidoPat IN ('FEDERICO', 'RAMIREZ', 'GIMENEZ') THEN 1 ELSE 0 END +
  CASE WHEN empApellidoMat IN ('FEDERICO', 'RAMIREZ', 'GIMENEZ') THEN 1 ELSE 0 END
) = 3

DELETE FROM EMPLEADOS
WHERE empId = -1

INSERT INTO EMPLEADOS
	VALUES( 9999, 'MENDOZA', 'URIAS', 'LUIS','M' )

SELECT * FROM Paises

SELECT * FROM VW_HECHOS_SOLICITUDES_OBJETIVOS
WHERE solIdTarjeta = 1

SELECT s.solIdTarjeta, i.* FROM 
	VW_HECHOS_SOLICITUDES_OBJETIVOS s
LEFT JOIN VW_IMPORTES i
ON s.solIdTarjeta = i.tranIdTarjeta
WHERE i.tranIdTarjeta IS NULL

SELECT * FROM Solicitudes
SELECT * FROM ControlIds