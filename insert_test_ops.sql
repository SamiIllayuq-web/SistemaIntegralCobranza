-- Script de prueba: insertar 50 operaciones para el cliente 1401
-- Ejecutar:
--   PGPASSWORD='raticulacion' psql -h localhost -U postgres -d cobranza -f insert_test_ops.sql

DO $$
DECLARE
    i INTEGER;
    agencia_id_val BIGINT;
BEGIN
    -- Buscar una agencia activa
    SELECT id INTO agencia_id_val FROM agencias WHERE activo=true LIMIT 1;
    IF agencia_id_val IS NULL THEN
        RAISE NOTICE 'No hay agencias activas. Usando id NULL.';
        agencia_id_val := NULL;
    ELSE
        RAISE NOTICE 'Usando agencia_id=%', agencia_id_val;
    END IF;

    FOR i IN 1..50 LOOP
        INSERT INTO operaciones (
            cliente_id,
            agencia_id,
            cuenta,
            numero_operacion,
            monto_capital,
            monto_total,
            dias_mora,
            moneda,
            situacion,
            estado,
            etapa,
            observacion,
            rango,
            analista,
            analista_senior,
            numero_expediente,
            tipo_proceso,
            tipo_juzgado,
            distrito_judicial,
            numero_juzgado,
            monto_demandado,
            escribano_legal,
            codigo_exp_cautelar,
            numero_ficha_registral,
            numero_partida,
            etapa_procesal,
            activo,
            fecha_creacion
        ) VALUES (
            1401,
            agencia_id_val,
            '44286' || LPAD(i::TEXT, 4, '0'),
            18000000 + i,
            (1000 + (i * 123))::DECIMAL(15,2),
            (2000 + (i * 187))::DECIMAL(15,2),
            (i * 7) % 365,
            'SOL',
            CASE i % 3
                WHEN 0 THEN 'ACTIVA'
                WHEN 1 THEN 'EN GESTION'
                ELSE 'CERRADA'
            END,
            CASE i % 4
                WHEN 0 THEN 'SOLVENTE'
                WHEN 1 THEN 'MORA'
                WHEN 2 THEN 'CASTIGO'
                ELSE 'SIN INFO'
            END,
            CASE i % 3
                WHEN 0 THEN 'PRE-JUDICIAL'
                WHEN 1 THEN 'JUDICIAL'
                ELSE 'POST-JUDICIAL'
            END,
            'Operacion de prueba #' || i || ' - Test de paginacion',
            CASE i % 3
                WHEN 0 THEN 'RANGO A'
                WHEN 1 THEN 'RANGO B'
                ELSE 'RANGO C'
            END,
            'Analista Test ' || (i % 3 + 1),
            'Senior Test ' || (i % 2 + 1),
            'EXP-' || LPAD(i::TEXT, 5, '0'),
            CASE i % 3
                WHEN 0 THEN 'COBRO'
                WHEN 1 THEN 'EJECUCION'
                ELSE 'SINGESTURA'
            END,
            'JUZGADO CIVIL',
            'LIMA',
            (i * 3)::INT,
            (5000 + (i * 211))::DECIMAL(15,2),
            'Dr. Escribano ' || (i % 4 + 1),
            'CAUT-2024-' || LPAD(i::TEXT, 4, '0'),
            'FICHA-' || LPAD(i::TEXT, 4, '0'),
            'PART-' || LPAD(i::TEXT, 4, '0'),
            CASE i % 5
                WHEN 0 THEN 'Postulatoria'
                WHEN 1 THEN 'Impugnatoria'
                WHEN 2 THEN 'Prueba'
                WHEN 3 THEN 'Sentencia'
                ELSE '—'
            END,
            true,
            NOW() - ((i * 7) * interval '1 day')
        );
    END LOOP;

    RAISE NOTICE '50 operaciones de prueba insertadas para cliente 1401.';
END $$;
