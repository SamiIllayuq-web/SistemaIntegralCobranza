cd ~/projects/cartera-cobranza
cat > AGENTS.md << 'EOF'
# AGENTS.md — Reglas del proyecto para agentes IA

## Agent skills

### Issue tracker
Issues live as markdown files in `tickets/`. See `docs/agents/issue-tracker.md`.

### Triage labels
_Triage skill not installed._

### Domain docs
Domain docs live in `docs/domain/` (glossary + README) + `docs/adr/`. See `docs/agents/domain.md`.

## Stack

- Java 21 + Spring Boot 3 + Thymeleaf + PostgreSQL
- Build: Maven
- Base de datos: PostgreSQL (Supabase Cloud)
- Tests: JUnit 5 + Mockito
- Excel: Apache POI
- Ubicación del código: `src/main/java/com/startup/cobranza/`
- Ubicación del proyecto: `/mnt/d/dev/gato/SistemaIntegralCobranza/`

## Reglas de runtime — LEER ANTES DE CUALQUIER TAREA

**La IA NUNCA debe correr la aplicación Spring Boot. Nunca.**

Comandos PROHIBIDOS para la IA:
- `mvn spring-boot:run`
- `./mvnw spring-boot:run`
- `java -jar target/*.jar`
- Cualquier comando que arranque el servidor embebido
- Comandos en modo watch / devtools
- Streams continuos sin timeout

Comandos PERMITIDOS para la IA (con timeout de 60s):
- `mvn compile`
- `mvn test-compile`
- `mvn test`
- `mvn package -DskipTests`
- `mvn dependency:resolve`
- `mvn clean`

Comandos SIEMPRE permitidos:
- Lectura de archivos
- `git status`, `git diff`, `git log`
- Búsquedas con `grep` / `rg`
- Edición de archivos de tickets (frontmatter, contenido markdown)

## Patrón de trabajo: AFK + HITL

Cada ticket tiene dos partes:

### Parte 1: AFK (la IA)
- Escribir el código
- Escribir los tests
- Correr `mvn compile` para verificar
- Correr `mvn test` con timeout
- Commit

### Parte 2: HITL (el humano)
- Levantar la app con `mvn spring-boot:run`
- Probar manualmente
- Reportar resultado a la IA
- Actualizar el frontmatter del ticket a status: done

## Comandos que el humano usa

(La IA debe DARME estos comandos, no ejecutarlos ella)

```bash
# Levantar la app
mvn spring-boot:run

# Probar endpoints
curl http://localhost:8080/clientes
curl -X POST http://localhost:8080/clientes -H "Content-Type: application/json" -d '{...}'

# Ver logs (en otra terminal)
tail -f logs/spring.log

# Matar el proceso si queda algo colgado
pkill -f spring-boot:run
# o Ctrl+C en la terminal donde corre