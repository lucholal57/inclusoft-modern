# Publicación inicial

## Flujo de ramas

- `develop`: trabajo diario y validación.
- `main`: versión publicada. Un merge desde `develop` a `main` dispara los deploys conectados.

## Neon: PostgreSQL

1. Crear un proyecto llamado `inclusoft-modern` en Neon con PostgreSQL.
2. Copiar desde Neon los valores de conexión y conservarlos sólo para Render:
   - `JDBC_DATABASE_URL`: URL JDBC. Debe comenzar con `jdbc:postgresql://` y conservar `sslmode=require`.
   - `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.

## Render: API

1. En Render, crear un Blueprint desde este repositorio. Detectará `render.yaml`.
2. Crear el servicio con nombre `inclusoft-api`; su URL esperada será `https://inclusoft-api.onrender.com`.
3. Completar las variables que solicita el Blueprint con los datos de Neon y una contraseña inicial:
   - `JDBC_DATABASE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
   - `INCLUSOFT_BOOTSTRAP_ADMIN_PASSWORD`.
4. Render genera automáticamente `INCLUSOFT_JWT_SECRET`. No se publica ningún secreto en GitHub.
5. Verificar `https://inclusoft-api.onrender.com/actuator/health`.

## Vercel: frontend

1. Importar el repositorio en Vercel con Root Directory `frontend`.
2. Vercel detecta Angular y utiliza `frontend/vercel.json`.
3. Elegir el nombre de proyecto `inclusoft-modern`, para obtener la URL corta `https://inclusoft-modern.vercel.app` si está disponible.
4. Probar login, alumnos, talleres y asistencia desde la URL publicada.

El frontend usa `/api/v1` y Vercel lo redirige internamente a Render. El navegador conserva una única URL pública y no necesita saber la URL del backend.
