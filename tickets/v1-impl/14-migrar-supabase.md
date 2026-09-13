---
id: 14
titulo: Migrar base de datos a Supabase (PostgreSQL cloud)
status: pending
prioridad: alta
tags: [supabase, base-de-datos, migracion]
created: 2026-09-13
---

## Contexto

El proyecto actualmente usa PostgreSQL local. Giordan quiere migrar a Supabase (PostgreSQL cloud).

## Objetivos

1. Configurar conexión JDBC del proyecto a la base de datos Supabase (production)
2. Generar/fresehar schema SQL con Flyway o script手工
3. Migrar datos existentes (si hay datos en la DB local que deben subirse)
4. Verificar que todas las queries / operaciones CRUD funcionen contra Supabase
5. Documentar variables de entorno / configuración para producción

## Pasos tentativos

### 1. Obtener credentials de Supabase

Necesario:
- `DB_URL` o `DATABASE_URL` de Supabase (形式: `postgresql://user:password@host:5432/dbname`)
- O conectar via connection string del dashboard de Supabase

### 2. Configurar `application.properties` / `application.yml`

```properties
spring.datasource.url=jdbc:postgresql://[HOST]:5432/[DBNAME]
spring.datasource.username=[USER]
spring.datasource.password=[PASSWORD]
```

### 3. Ejecutar schema

Generar DDL de las tablas actuales:

```bash
pg_dump --schema-only -U [USER] -d [DBNAME] > schema.sql
```

Luego aplicar en Supabase o usar las entidades JPA para auto-generar con `spring.jpa.hibernate.ddl-auto=update` (no recomendado para producción — mejor script manual).

### 4. Migrar datos

Si hay datos locales que deben subirse, dump + restore:

```bash
pg_dump --data-only -U [USER] -d [DBNAME] > data.sql
psql -h [HOST] -U [USER] -d [DBNAME] < data.sql
```

### 5. Testing

- Levantar app apuntando a Supabase
- Probar: importar Excel, bandeja clientes, expedientes, operaciones CRUD
- Verificar que no haya errores de conexión o timeouts

## Constraints

- Mantener backward compatibility con el schema existente
- No romper la BD local de desarrollo
- Usar variables de entorno para secrets, no hardcodear

## Archivos a modificar

- `src/main/resources/application.properties` (o `.yml`)
- Posible `.env` file para local dev
- `docs/` con nuevas credenciales/instrucciones de deployment
