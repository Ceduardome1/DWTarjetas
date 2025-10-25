USE DWTarjetas
GO
--PROCEDIMIENTOS:
CREATE PROCEDURE miSp_ActualizarReferencias
AS
BEGIN

--CLIENTES:
	UPDATE c
		SET c.clId = ctl.idFinal
	FROM Clientes AS c
	INNER JOIN ControlIds AS ctl
		ON c.clId = ctl.idInicial
		AND ctl.tipo = 'C';

	UPDATE sol
		SET sol.solClId = ctl.idFinal
	FROM Solicitudes AS sol
	INNER JOIN ControlIds AS ctl
		ON sol.solClId = ctl.idInicial 
		AND ctl.tipo = 'C';

--EMPLEADOS:
	UPDATE e
	SET e.empId = ctl.idFinal
	FROM Empleados AS e
	INNER JOIN ControlIds AS ctl
		ON e.empId = ctl.idInicial
		AND ctl.tipo = 'E';

	UPDATE sol
	SET sol.solEmpId = ctl.idFinal
	FROM Solicitudes AS sol
	INNER JOIN ControlIds AS ctl
		ON sol.solEmpId = ctl.idInicial 
		AND ctl.tipo = 'E';
END
GO

--ACTUALIZAR REFERENCIAS.
print '-------------------------'
print 'ACTUALIZAR REFERENCIAS:'
BEGIN TRAN
	BEGIN TRY

		EXEC miSp_ActualizarReferencias

		COMMIT TRAN
print 'TRANSACCION EXITOSA'

	END TRY
	BEGIN CATCH
		ROLLBACK TRAN
print 'TRANSACCION DESHECHA'
print ERROR_MESSAGE()
	END CATCH
GO

--INTEGRIDAD REFERENCIAL:
ALTER TABLE Solicitudes 
	ADD
		CONSTRAINT FK_Sol_Emp FOREIGN KEY( solEmpId ) REFERENCES Empleados( empId ),
		CONSTRAINT FK_Sol_Cl FOREIGN KEY( solClId ) REFERENCES Clientes( clId ),
		CONSTRAINT FK_Sol_Pais FOREIGN KEY( solPaisId ) REFERENCES Paises ( paisId ),
		CONSTRAINT FK_Sol_Red FOREIGN KEY( solRedId ) REFERENCES Redes ( redId ),
		CONSTRAINT FK_Sol_dimTiempo_Fecha FOREIGN KEY( solFecha ) REFERENCES dimensionTiempo( fecha )

ALTER TABLE Transacciones
	ADD
--		CONSTRAINT FK_Sol_Tarjeta FOREIGN KEY( tranIdTarjeta ) REFERENCES Solicitudes ( solIdTarjeta ),
		CONSTRAINT FK_Tran_Pais FOREIGN KEY( tranPaisId ) REFERENCES Paises ( paisId )
GO

/*
CREATE PROCEDURE miSp_ContabilizarObjetivos
AS
BEGIN

    INSERT Objetivos ( objFecha, objEmpId, objMeta, objClientesAtendidos, objClientesDistintosAtendidos, objColocaciones )
    SELECT 
        s.solFecha,
        s.solEmpId,
        MAX(s.solObjetivo),
        COUNT( s.solClId ),
		COUNT( DISTINCT s.solClId ),
        SUM(
			CASE 
				WHEN s.solColocada = 'Y' 
			THEN 1 ELSE 0 END
		)
    FROM SOLICITUDES s
    GROUP BY s.solFecha, s.solEmpId

END
GO	

--CONTABILIZAR OBJETIVOS.
print '-------------------------'
print 'CONTANDO OBJETIVOS:'
BEGIN TRAN
	BEGIN TRY

		EXEC miSp_ContabilizarObjetivos

		COMMIT TRAN
print 'TRANSACCION EXITOSA'

	END TRY
	BEGIN CATCH
		ROLLBACK TRAN
print 'TRANSACCION DESHECHA'
print ERROR_MESSAGE()
	END CATCH
GO

ALTER TABLE Objetivos 
	ADD 
		CONSTRAINT FK_Obj_dimTiempo_Fecha FOREIGN KEY( objFecha ) REFERENCES dimensionTiempo( fecha )

ALTER TABLE Solicitudes 
	ADD
		CONSTRAINT FK_Sol_Obj_Fecha_Emp FOREIGN KEY( solFecha, solEmpId ) REFERENCES Objetivos( objFecha, objEmpId )
*/