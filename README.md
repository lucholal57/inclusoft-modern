# INCLUsoft Modern

Reconstrucción moderna de INCLUsoft para la gestión integral de un instituto CAAD.

El proyecto reemplaza la implementación legacy, sin migración de datos ni reutilización de su código. Conserva su referencia funcional y la reordena en módulos independientes, con una API Java/Spring Boot y una aplicación Angular.

## Estructura

- `backend/`: API REST con Spring Boot.
- `frontend/`: aplicación web Angular.
- `docs/`: decisiones y alcance funcional.

## Alcance de la primera versión

- Acceso por rol: administración, dirección, vicedirección y docentes.
- Alumnos y ficha: contactos, salud, apoyos y autorizaciones.
- Personal, usuarios y talleres con agenda, equipo y alumnos asignados.
- Registro de asistencia por taller.
- Resumen operativo para dirección.

No hay datos legacy para migrar.

## Ejecución local

Requiere Java 21+, Node.js 20+ y PostgreSQL local.

```text
cd backend
$env:INCLUSOFT_DB_PASSWORD='tu-password-local'
mvn spring-boot:run

cd ../frontend
npm install
npm start
```

La API se expone en `http://localhost:8080/api/v1/health`, y el estado de conexión a la base puede verificarse en `http://localhost:8080/actuator/health`. El frontend se expone en `http://localhost:4200`.

Para la publicación en Vercel, Render y PostgreSQL consultar [docs/deployment.md](docs/deployment.md).
