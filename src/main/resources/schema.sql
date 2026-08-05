-- Eliminar columna empresa_id huérfana de clientes (sobra del modelo viejo,
-- donde Cliente tenía relación directa con Empresa. El modelo nuevo es:
-- Cliente → Operacion → Empresa)
ALTER TABLE clientes DROP COLUMN IF EXISTS empresa_id;

-- Hacer expediente_id nullable (BienEmbargado ahora apunta a Operacion, no Expediente)
-- La entidad BienEmbargado tiene ambas relaciones; expediente_id es legacy.
ALTER TABLE bienes_embargados ALTER COLUMN expediente_id DROP NOT NULL;

-- Agregar columna operacion_id si no existe (nueva FK en el modelo refactorizado)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'bienes_embargados' AND column_name = 'operacion_id'
    ) THEN
        ALTER TABLE bienes_embargados ADD COLUMN operacion_id BIGINT;
        ALTER TABLE bienes_embargados ADD CONSTRAINT fk_bien_operacion
            FOREIGN KEY (operacion_id) REFERENCES operaciones(id) ON DELETE SET NULL;
    END IF;
END $$;
