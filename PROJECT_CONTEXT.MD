# PROJECT_CONTEXT.md

# ==========================================
# SISTEMA INTEGRAL DE COBRANZA
# PROJECT CONTEXT
# ==========================================

# PROPÓSITO

Este archivo es la única fuente oficial de verdad del proyecto.

Todos los agentes deben leer este archivo antes de generar cualquier código.

Si una instrucción contradice este documento, prevalece PROJECT_CONTEXT.md.

Los agentes no pueden modificar la arquitectura, tecnologías, reglas del negocio ni el alcance del MVP.

---

# FILOSOFÍA DEL PROYECTO

Este proyecto sigue una estrategia MVP.

Principios:

- Resolver el problema actual del cliente.
- Construir únicamente lo necesario.
- No agregar funcionalidades no solicitadas.
- Preferir simplicidad antes que complejidad.
- Evitar sobreingeniería.
- Escribir código limpio.
- Escribir código mantenible.
- Mantener una arquitectura simple.
- Terminar una etapa antes de comenzar la siguiente.

Si existen dos soluciones posibles, elegir siempre la más sencilla.

---

# DESCRIPCIÓN DEL PROYECTO

Sistema web para un estudio jurídico especializado en recuperación de créditos extrajudiciales.

El sistema permitirá:

- administrar clientes
- administrar empresas
- administrar agencias
- importar cartera desde Excel
- registrar gestiones
- registrar compromisos de pago
- consultar información mediante API Kmente

No existirán usuarios externos.

Únicamente utilizarán el sistema:

- Administrador
- Secretario

---

# OBJETIVO

Digitalizar el proceso de cobranza que actualmente se administra mediante archivos Excel.

Organizar la cartera de clientes.

Centralizar la información.

Reducir tareas manuales.

Facilitar el seguimiento de clientes.

---

# ALCANCE DEL MVP

El MVP únicamente incluye:

- Login
- Usuarios
- Empresas
- Agencias
- Importación de cartera
- Clientes
- Gestiones
- Compromisos
- Consulta Kmente

Todo lo demás queda fuera del MVP.

---

# ARQUITECTURA

Arquitectura aprobada:

Monolito Modular

Toda la aplicación será desarrollada utilizando Spring Boot.

No existirán proyectos separados para frontend y backend.

El frontend será construido utilizando Thymeleaf.

Toda la lógica de negocio pertenece al backend.

---

# STACK TECNOLÓGICO

Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- RestClient
- Maven

Frontend

- Thymeleaf
- Bootstrap 5
- HTML5
- CSS3
- JavaScript Vanilla

Base de datos

- PostgreSQL

Proveedor BD

- Supabase Cloud

Excel

- Apache POI

Control de versiones

- Git
- GitHub

---

# TECNOLOGÍAS NO APROBADAS

No utilizar:

- React
- Angular
- Vue
- Docker
- Redis
- Microservicios
- WebFlux
- Programación Reactiva
- Librerías innecesarias

---

# ESTRUCTURA DEL PROYECTO

Proyecto único Spring Boot.

Estructura esperada:

src/main/java

src/main/resources

templates/

static/

application.yml

pom.xml

mvnw

---

# DESPLIEGUE

Desarrollo

- Spring Boot Local
- Supabase Cloud

Producción

- Railway
- Supabase Cloud

No modificar código para desplegar.

Únicamente cambiar variables de entorno.

---

# VARIABLES DE ENTORNO

Nunca almacenar:

- usuarios
- contraseñas
- JWT Secret
- API Keys
- credenciales

Toda configuración sensible deberá utilizar variables de entorno.

---

# GIT

Trabajar mediante ramas.

Ejemplo:

feature/login

feature/clientes

feature/importacion

feature/kmente

Commits descriptivos.

No trabajar directamente sobre main.

---

# GITIGNORE

Nunca subir:

.env

.env.*

application-local.yml

target/

logs/

node_modules/

.idea/

.vscode/

*.log

credenciales

archivos temporales

---

# REGLAS DE DESARROLLO

Mantener clases pequeñas.

Mantener métodos pequeños.

No duplicar código.

No escribir código innecesario.

No anticipar funcionalidades futuras.

No escribir lógica de negocio en Controllers.

Utilizar nombres descriptivos.

Mantener separación de responsabilidades.

---

# KMENTE

La API será utilizada únicamente para consultas.

El frontend nunca consumirá Kmente.

Siempre:

Usuario

↓

Spring Boot

↓

API Kmente

No sincronizar información masiva.

Consultar únicamente cuando el usuario lo solicite.

---

# ROLES

Administrador

Acceso total.

Secretario

Acceso restringido.

---

# AGENTES

## Hermes

Responsabilidad

Orquestar todo el desarrollo.

Nunca modificar la arquitectura.

Nunca modificar el stack.

---

## Context Agent

Responsabilidad

Leer PROJECT_CONTEXT.md

Validar:

- arquitectura
- stack
- reglas
- MVP
- etapa actual

Nunca generar código.

---

## Backend Agent

Responsabilidad

Generar únicamente backend.

Nunca generar frontend.

---

## Frontend Agent

Responsabilidad

Generar únicamente:

Thymeleaf

Bootstrap

HTML

CSS

JavaScript

Nunca generar Java.

---

## Reviewer Agent

Responsabilidad

Revisar.

Nunca programar.

Validar:

- simplicidad
- arquitectura
- duplicación
- buenas prácticas
- cumplimiento del PROJECT_CONTEXT.md

---

# ROADMAP

## ETAPA 1

Configuración inicial

- Proyecto Spring Boot
- Login
- JWT
- Usuarios
- Empresas
- Agencias

---

## ETAPA 2

Clientes

- Importación Excel
- Listado
- Detalle Cliente

---

## ETAPA 3

Gestiones

- Registrar gestión
- Historial
- Compromisos

---

## ETAPA 4

Consulta Kmente

- Consulta DNI
- Visualización información

---

# ESTADO ACTUAL

Etapa activa:

ETAPA 1

Actualizar esta sección conforme avance el desarrollo.

---

# REGLAS PARA LOS AGENTES

Antes de escribir código verificar:

1. ¿La funcionalidad pertenece al MVP?

Si NO

Detener.

2. ¿Respeta la arquitectura?

Si NO

Detener.

3. ¿Respeta el stack aprobado?

Si NO

Detener.

4. ¿La tarea corresponde a la etapa actual?

Si NO

Detener.

5. ¿Existe una solución más simple?

Si SI

Elegir la solución simple.

---

# OBJETIVO FINAL

Construir un MVP funcional.

No construir un sistema perfecto.

El sistema crecerá mediante nuevas versiones.

La prioridad siempre será:

Simplicidad.

Legibilidad.

Mantenibilidad.

Calidad del código.
