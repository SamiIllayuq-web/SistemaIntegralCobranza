-- Insertar 50 clientes de prueba para test de paginación
-- Ejecutar:
--   PGPASSWORD='raticulacion' psql -h localhost -U postgres -d cobranza -f insert_50_clientes.sql

DO $$
DECLARE
    i INTEGER;
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO clientes (
            nombre_completo,
            dni,
            telefono,
            telefono2,
            telefono3,
            email,
            direccion,
            activo,
            fecha_creacion
        ) VALUES (
            'CLIENTE PRUEBA ' || LPAD(i::TEXT, 3, '0'),
            '7' || LPAD((10000000 + i)::TEXT, 8, '0'),
            '9' || LPAD((90000000 + i)::TEXT, 8, '0'),
            NULL,
            NULL,
            'cliente' || LPAD(i::TEXT, 3, '0') || '@test.com',
            'Av. Prueba ' || i || ' - Lima',
            true,
            NOW() - ((i * 3) * interval '1 day')
        );
    END LOOP;

    RAISE NOTICE '50 clientes de prueba insertados.';
END $$;
