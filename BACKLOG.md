# Backlog — Replicación de la aplicación

Lista de tareas para replicar el proyecto desde cero. Cada funcionalidad se implementa en su propia rama y se integra siguiendo el orden de dependencias indicado.

## Fase 0: Base y datos

| # | Tarea | Rama sugerida | Criterios de aceptación |
|---|-------|--------------|--------------------------|
| 1 | **Base del proyecto JavaFX** — crear estructura de paquetes `controller`, `dao`, `model`, `util`, `system`, `view` y `Principal` que cargue una vista placeholder. | `feature/base-proyecto` | El proyecto compila y abre una ventana con la vista de login (vacía). |
| 2 | **Conexión a BD** — singleton `Conexion` (MySQL) y `SecurityUtil.hashSHA256`. | `feature/conexion-db` | `Conexion.getInstancia().conectar()` devuelve una conexión válida; el hash SHA-256 es estable. |
| 3 | **Scripts SQL** — DDL de tablas (usuarios, libros, autores, categorías, editoriales, autor_libro, clientes, ventas, detalle_venta, inventario) + procedimientos almacenados (sp_iniciar_sesion, sp_crear_usuario, sp_\* para cada CRUD). | `feature/estructura-db` | Script ejecutable en MySQL; cada entidad tiene su SP de listar/crear/actualizar/eliminar. |
| 4 | **Modelos de entidades** — clases `Usuario`, `Libro`, `Autor`, `Categoria`, `Editorial`, `AutorLibro`, `Cliente`, `Venta`, `DetalleVenta`, `LineaVenta` con sus campos y getters/setters. | `feature/modelo-entidades` | Todos los modelos mapean las columnas de la BD. |

## Fase 1: Arquitectura y autenticación

| # | Tarea | Rama sugerida | Criterios de aceptación |
|---|-------|--------------|--------------------------|
| 5 | **Patrón DAO** — interfaces base `Dao`/`Crud` y `UsuarioDAO` como interfaz. | `feature/patron-dao` | Existen interfaces para el módulo usuario listas para implementar. |
| 6 | **Inicio de sesión** — controlador que valida campos, cifra la contraseña, llama a `sp_iniciar_sesion` y muestra alertas (éxito/error/validación). | `feature/login` | Con credenciales válidas muestra alerta de éxito; con inválidas, error. |
| 7 | **Registro de usuario** — vista + controlador que crea usuario con rol y contraseña cifrada. | `feature/registro-usuario` | Desde el login se navega al registro y se puede crear un usuario nuevo. |
| 8 | **Contexto de sesión** — singleton `SesionContext` con usuario actual, `cerrarSesion()` y rol. | `feature/sesion-context` | El usuario queda guardado tras el login y disponible desde cualquier controlador. |
| 9 | **Navegación centralizada** — `Principal.cambiarEscena(ruta)` en una única ventana; dashboard según rol (admin/empleado). | `feature/navegacion` | El login redirige al dashboard sin abrir ventanas nuevas. |

## Fase 2: Módulos de gestión (CRUD)

| # | Tarea | Rama sugerida | Criterios de aceptación |
|---|-------|--------------|--------------------------|
| 10 | **Panel de administración** — vista con barra lateral, tarjetas de acceso (nuevo libro, ventas, inventario, usuarios, reportes, configuración) y bienvenida con nombre/rol. | `feature/dashboard` | Todas las tarjetas/íconos navegan a la vista correspondiente (o quedan placeholder). |
| 11 | **Validaciones centralizadas** — `ValidacionException` (no vacío, email, numérico). | `feature/validaciones` | Los controladores lanzan la excepción y muestran su mensaje en alerta. |
| 12 | **CRUD Usuarios** — `UsuarioDAOImpl` + vista con tabla filtrable (crear, actualizar, cambiar contraseña, desactivar, eliminar, listar, buscar por id). | `feature/crud-usuarios` | Todas las operaciones del SP se ejecutan y la tabla se refresca. |
| 13 | **CRUD Categorías** — DAO + vista. | `feature/crud-categorias` | Alta/baja/modificación y listado funcionales. |
| 14 | **CRUD Editoriales** — DAO + vista. | `feature/crud-editoriales` | Alta/baja/modificación y listado funcionales. |
| 15 | **CRUD Autores** — DAO + vista. | `feature/crud-autores` | Alta/baja/modificación y listado funcionales. |
| 16 | **CRUD Libros** — DAO + vista con campos completos (ISBN, precio, stock, estado, fechas). | `feature/crud-libros` | Alta/baja/modificación y listado funcionales. |
| 17 | **Relación Autor–Libro** — DAO + vista de asociación. | `feature/autor-libro` | Se pueden asignar autores a un libro. |
| 18 | **CRUD Clientes** — DAO + vista. | `feature/crud-clientes` | Alta/baja/modificación y listado funcionales. |

## Fase 3: Comercial y presentación

| # | Tarea | Rama sugerida | Criterios de aceptación |
|---|-------|--------------|--------------------------|
| 19 | **Gestión de Ventas** — `VentaDAOImpl` + `VentaView` (crear venta). | `feature/ventas` | Se registra una venta y persiste en BD. |
| 20 | **Detalle de Venta** — `DetalleVentaDAOImpl` + `DetalleVentaView` con líneas por artículo. | `feature/detalle-venta` | Una venta puede contener varios artículos con cantidades. |
| 21 | **Lista de Ventas** — `ListaVentasView` para consultar ventas registradas. | `feature/lista-ventas` | Se listan las ventas con su detalle. |
| 22 | **Inventario** — `InventarioController` + `InventarioView` de existencias. | `feature/inventario` | Se muestra el stock disponible por libro. |
| 23 | **Estilos CSS** — `base.css` y estilos del dashboard; limpiar CSS de login. | `feature/estilos` | La app se ve coherente en todas las vistas. |

## Fase 4: Mejoras finales

| # | Tarea | Rama sugerida | Criterios de aceptación |
|---|-------|--------------|--------------------------|
| 24 | **Credenciales externalizadas** — `Conexion` lee `db.url/db.user/db.password` desde `src/db.properties` (gitignored) con plantilla `db.properties.example` versionada. | `feature/db-properties` | **HECHO** (commit `f325167`) — no hay credenciales en código versionado; si falta el archivo, la app falla con mensaje claro. |
| 25 | **Normalización de fin de línea** — `.gitattributes` con `eol=lf` para texto y `binary` para assets. | `chore/line-endings` | **HECHO** (commit `0f61cd3`) — `git diff` ya no muestra ruido CRLF/LF. |
| 26 | **Dashboards por rol** — vistas FXML + controladores para `empleado` (inventario + alta de entidades, sin usuarios) y `cajero` (proceso de ventas); el login redirige según rol. | `feature/dashboards-rol` | Con cada rol el login abre su dashboard; el empleado no ve usuarios; el cajero no ve CRUD de entidades. |
| 27 | **Errores no tragados** — `DaoException` en la capa DAO: los `DAOImpl` lanzan en vez de devolver `false/null` en silencio; los controladores muestran alerta con el mensaje. | `feature/dao-exception` | Si la BD está caída o falla un SP, la UI muestra un error claro (no una tabla vacía ni un `false` mudo). |
| 28 | **Scripts SQL versionados** — DDL + procedimientos almacenados + datos semilla en `sql/` (fuera de `src/` para no copiarse al classpath). | `feature/sql-scripts` | La BD se puede recrear desde cero con `mysql < sql/01-schema.sql` etc. |
| 29 | **Refactor de duplicación en DAOs** — helper `JdbcTemplate` + `RowMapper` en `org.ac.util`; los `DAOImpl` pasan a ser declarativos (solo SP + mapeo). | `refactor/dao-template` | El boilerplate de conexión/`prepareCall` vive en un solo lugar; cada `DAOImpl` reduce ~40% de código. |

## Convenciones para quien replique

- **Flujo por rama**: `git checkout -b <rama>` → implementar → merge a `develop` (o `main`), siguiendo el orden de dependencias (no se puede hacer `crud-libros` sin `conexion-db`, `estructura-db` y `patron-dao`).
- **DoD común**: compila sin errores, la operación persiste en BD vía SP, la vista se refresca y se muestra alerta ante error/validación.
- Las tareas 12–18 son independientes entre sí y pueden hacerse en paralelo tras la tarea 5.

nota: 
**Se parte de un script de DB ya construido, una primera fase del desarrollo de este sistema.
Se solicita implementar respecto a scrip anteriores los siguientes cambios:** 
- compras a ventas,
- detalle_compra a detalle_venta 
- existencias en libro
- si hay fechas a date 
- agregar usuario a venta
- detalle_venta agregar precio por unidad y total
- procedimientos para usuario; CRUD, update sin clave y solo cambio de clave.

Agregar nombre, apellido y correo a usuario.
