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

CREATE PROCEDURE miSp_ContabilizarObjetivos
AS
BEGIN

    INSERT Objetivos ( objFecha, objEmpId, objMeta, objClientesAtendidos, objClientesDistintosAtendidos, objColocaciones )
    SELECT 
        s.solFecha,
        s.solEmpId,
        MAX(s.solObjetivo),
        COUNT( solClId ),
		COUNT( DISTINCT solClId ),
        SUM(
			CASE 
				WHEN s.solColocada = 'Y' 
			THEN 1 ELSE 0 END
		)
    FROM SOLICITUDES s
    GROUP BY s.solFecha, s.solEmpId

END
GO	

--ACTUALIZAR REFERENCIAS.
print '\n-------------------------'
print 'ACTUALIZAR REFERENCIAS:'
BEGIN TRAN
	BEGIN TRY

		EXEC miSp_ActualizarReferencias

--INTEGRIDAD REFERENCIAL:
	ALTER TABLE Solicitudes 
		ADD CONSTRAINT PK_Sol PRIMARY KEY NONCLUSTERED ( solFecha, solEmpId ),
		CONSTRAINT FK_Sol_Emp FOREIGN KEY( solEmpId ) REFERENCES Empleados( empId ),
		CONSTRAINT FK_Sol_Cl FOREIGN KEY( solClId ) REFERENCES Clientes( clId )

		COMMIT TRAN
print 'TRANSACCION EXITOSA'

	END TRY
	BEGIN CATCH
		ROLLBACK TRAN
print 'TRANSACCION DESHECHA'
print ERROR_MESSAGE()
	END CATCH
GO

--CONTABILIZAR OBJETIVOS.
print '\n-------------------------'
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