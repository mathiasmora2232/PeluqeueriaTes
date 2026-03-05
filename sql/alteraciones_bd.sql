-- ============================================
-- Script de mejoras a la base de datos
-- Ejecutar en PostgreSQL (peluqueria)
-- ============================================

-- 1. Agregar estilista_id a citas (relacion directa)
ALTER TABLE citas ADD COLUMN IF NOT EXISTS estilista_id INTEGER;

-- Migrar datos existentes: extraer estilista de observaciones
UPDATE citas c SET estilista_id = e.id_usuario
FROM estilistas e
WHERE c.observaciones LIKE '%' || e.nombre || '%'
AND c.estilista_id IS NULL;

-- Agregar foreign key
ALTER TABLE citas ADD CONSTRAINT fk_citas_estilista
    FOREIGN KEY (estilista_id) REFERENCES estilistas(id_usuario);

-- 2. Email UNIQUE en clientes
-- Primero limpiar duplicados vacios
UPDATE clientes SET email = NULL WHERE email = '';
ALTER TABLE clientes ADD CONSTRAINT uq_clientes_email UNIQUE (email);

-- 3. Agregar created_at a tablas que no lo tienen
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE citas ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE facturas ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
