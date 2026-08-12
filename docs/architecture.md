# Arquitectura y alcance inicial

## Decisiones de fundación

| Área | Decisión inicial |
| --- | --- |
| Backend | Spring Boot, API REST versionada bajo `/api/v1` |
| Frontend | Angular standalone con router y `HttpClient` |
| Persistencia | Pendiente de selección; no se crea ni migra una base legacy |
| Seguridad | Pendiente de definir identidad, recuperación de credenciales y roles |
| Contrato | OpenAPI será la fuente de verdad antes de sumar cada módulo |

## Alcance funcional tomado como referencia

| Dominio | Capacidades legacy a reconstruir |
| --- | --- |
| Acceso | inicio y cierre de sesión, administración de usuarios |
| Alumnos | ficha, datos adicionales, asistencia, antecedentes, patologías, enfermería y acta compromiso |
| Personal | ficha, asistencia, permisos, evaluación laboral y entrega de proyectos |
| Talleres | talleres, informes, materiales, producción, compras, ventas e inventario |
| Operación | acompañantes, viajes, biblioteca y cooperadora |
| Consulta | estadísticas operativas |

## Límites de esta etapa

Esta fundación no incorpora autenticación real, entidades de negocio, persistencia, datos de prueba ni pantallas de gestión. Solo define la separación de aplicaciones, un endpoint técnico de salud y una vista de bienvenida para validar la comunicación futura.

## Próxima decisión necesaria

Antes de implementar el primer módulo se debe confirmar:

1. Base de datos objetivo y mecanismo de ejecución local.
2. Perfiles y permisos de cada tipo de usuario.
3. Prioridad del primer flujo de negocio (se sugiere alumnos y legajo).
