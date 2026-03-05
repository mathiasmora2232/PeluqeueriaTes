-- ============================================
-- Script de mejoras a la base de datos
-- Ejecutar en PostgreSQL (peluqueria)
-- ============================================

-- 1. Agregar estilista_id a citas (relacion directa con estilistas)
ALTER TABLE citas ADD COLUMN IF NOT EXISTS estilista_id INTEGER;

-- Migrar datos existentes: extraer estilista de observaciones
UPDATE citas c SET estilista_id = e.id_usuario
FROM estilistas e
WHERE c.observaciones LIKE '%' || e.nombre || '%'
AND c.estilista_id IS NULL;

-- Agregar foreign key (solo si no existe)
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_citas_estilista') THEN
        ALTER TABLE citas ADD CONSTRAINT fk_citas_estilista
            FOREIGN KEY (estilista_id) REFERENCES estilistas(id_usuario);
    END IF;
END $$;

-- 2. Agregar columna estado para soft delete en tablas que no la tienen
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS estado VARCHAR(20) DEFAULT 'Activo';
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS estado VARCHAR(20) DEFAULT 'Activo';
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS estado VARCHAR(20) DEFAULT 'Activo';

-- 3. Email UNIQUE en clientes (limpiar vacios primero)
UPDATE clientes SET email = NULL WHERE email = '';
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_clientes_email') THEN
        ALTER TABLE clientes ADD CONSTRAINT uq_clientes_email UNIQUE (email);
    END IF;
END $$;
