# Publicación inicial

## Flujo de ramas

- `develop`: trabajo diario y validación.
- `main`: versión publicada. Un merge desde `develop` a `main` dispara los deploys conectados.

## Render: API y PostgreSQL

1. En Render, crear un Blueprint desde este repositorio. Detectará `render.yaml`.
2. Crear el servicio con nombre `inclusoft-api`; su URL esperada será `https://inclusoft-api.onrender.com`.
3. Crear la base `inclusoft-postgres` y copiar sus datos de conexión internos a las variables solicitadas por el Blueprint:
   - `JDBC_DATABASE_URL`: URL interna de la base con prefijo `jdbc:`. Ejemplo: `jdbc:postgresql://host:5432/inclusoft`.
   - `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
   - `INCLUSOFT_BOOTSTRAP_ADMIN_PASSWORD`: contraseña inicial del administrador.
4. Render genera automáticamente `INCLUSOFT_JWT_SECRET`. No se publica ningún secreto en GitHub.
5. Verificar `https://inclusoft-api.onrender.com/actuator/health`.

## Vercel: frontend

1. Importar el repositorio en Vercel con Root Directory `frontend`.
2. Vercel detecta Angular y utiliza `frontend/vercel.json`.
3. Elegir el nombre de proyecto `inclusoft-modern`, para obtener la URL corta `https://inclusoft-modern.vercel.app` si está disponible.
4. Probar login, alumnos, talleres y asistencia desde la URL publicada.

El frontend usa `/api/v1` y Vercel lo redirige internamente a Render. El navegador conserva una única URL pública y no necesita saber la URL del backend.
