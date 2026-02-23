# BsmartOne API

## Configuración requerida (DB + variables)

Esta aplicación toma configuración desde:

1. **Variables de entorno / `application.properties`**: conexión base a la BD, puertos, JWT, mail, etc.
2. **Tablas de configuración en BD**: parámetros del flujo de sincronización (`REST OAuth -> DB -> CSV -> Oracle`).

---

## 1) Conexión a base de datos (Spring Boot)

Archivo: `src/main/resources/application.properties`

Parámetros que debes configurar (vía environment variables recomendadas):

- `DB_URL` (ej. `jdbc:postgresql://localhost:5432/bsmartone`)
- `DB_USERNAME`
- `DB_PASSWORD`
- `HIBERNATE_DIALECT` (por defecto: `org.hibernate.dialect.PostgreSQLDialect`)
- `JPA_DDL_AUTO` (recomendado `none` en ambientes gestionados con Flyway)
- `FLYWAY_ENABLED` (`true` para ejecutar migraciones)

Ejemplo:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/bsmartone'
export DB_USERNAME='bsmartone'
export DB_PASSWORD='secret'
export HIBERNATE_DIALECT='org.hibernate.dialect.PostgreSQLDialect'
export JPA_DDL_AUTO='none'
export FLYWAY_ENABLED='true'
```

---

## 2) Variables del flujo de sincronización (en BD)

Las variables de negocio del pipeline se configuran en tablas creadas por Flyway:

- `application_parameters`
- `entity_configuration`
- `entity_field_mapping`

### 2.1 `application_parameters`

Aquí va la configuración global de OAuth, API, paginación y carga Oracle.

Campos importantes:

- `oauth_token_url`
- `oauth_client_id`
- `oauth_client_secret`
- `oauth_scope`
- `api_base_url`
- `api_resource_path` (opcional si aplica)
- `batch_size` (ej. 200)
- `initial_offset` (normalmente 0)
- `oracle_upload_url`
- `oracle_upload_request_template` (opcional)
- `oracle_target_filename`
- `enabled` (**debe haber uno activo**)

Ejemplo:

```sql
INSERT INTO application_parameters (
  oauth_token_url,
  oauth_client_id,
  oauth_client_secret,
  oauth_scope,
  api_base_url,
  api_resource_path,
  batch_size,
  initial_offset,
  oracle_upload_url,
  oracle_upload_request_template,
  oracle_target_filename,
  enabled,
  updated_at
) VALUES (
  'https://oauth.proveedor.com/token',
  'client-id',
  'client-secret',
  'read:data',
  'https://api.proveedor.com',
  '/v1/resources',
  200,
  0,
  'https://oracle-upload-endpoint/upload',
  '{"fileName":"${fileName}","path":"${filePath}"}',
  'export.csv',
  true,
  now()
);
```

### 2.2 `entity_configuration`

Define cada entidad exportable.

Campos:

- `entity_name` (identificador lógico; se usa en endpoint `/api/v1/sync/{entityName}/start`)
- `endpoint_path` (ruta REST para esa entidad)
- `country_field` (opcional)
- `file_name`
- `delimiter` (`,` o `;`)
- `line_separator` (`\n`)
- `enabled`

Ejemplo:

```sql
INSERT INTO entity_configuration (
  entity_name,
  endpoint_path,
  country_field,
  file_name,
  delimiter,
  line_separator,
  enabled,
  notes
) VALUES (
  'providers',
  '/v1/providers',
  'country.code',
  'providers.csv',
  ',',
  E'\n',
  true,
  'Exportación de proveedores'
);
```

### 2.3 `entity_field_mapping`

Define orden y rutas JSON para armar `record_text`.

Campos:

- `entity_configuration_id`
- `field_order`
- `field_name`
- `json_path` (ej. `provider.pointId`)
- `default_value`
- `enabled`

Ejemplo:

```sql
INSERT INTO entity_field_mapping (
  entity_configuration_id,
  field_order,
  field_name,
  json_path,
  default_value,
  enabled
)
SELECT id, 1, 'point_id', 'provider.pointId', '', true
FROM entity_configuration WHERE entity_name = 'providers';

INSERT INTO entity_field_mapping (
  entity_configuration_id,
  field_order,
  field_name,
  json_path,
  default_value,
  enabled
)
SELECT id, 2, 'country', 'country.code', 'NA', true
FROM entity_configuration WHERE entity_name = 'providers';
```

---

## 3) Ejecución del flujo

1. Levantar aplicación.
2. Verificar migraciones Flyway.
3. Insertar configuración en tablas anteriores.
4. Iniciar ejecución:

```http
POST /api/v1/sync/{entityName}/start
```

5. Consultar estado/progreso:

```http
GET /api/v1/sync/{runId}
```

---

## 4) Dónde configurar cada cosa (resumen rápido)

- **Conexión PostgreSQL / puerto / JWT / mail**: `application.properties` + variables de entorno.
- **OAuth/API/batch/Oracle**: tabla `application_parameters`.
- **Entidad + endpoint + delimitador archivo**: tabla `entity_configuration`.
- **Campos JSON a texto (orden CSV)**: tabla `entity_field_mapping`.
