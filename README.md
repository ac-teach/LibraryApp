# LibraryApp — Sistema de Gestión de Librería

Aplicación de escritorio construida con **JavaFX** y **MySQL** para la gestión de una librería: usuarios, libros, autores, editoriales, categorías, clientes, ventas e inventario.

> **Propósito educativo**
>
> Este proyecto fue construido a nivel **educativo** para demostrar los alcances y aptitudes adquiridas por **estudiantes de informática** en el **curso de Programación 1**. Sirve como evidencia de aprendizaje sobre programación orientada a objetos, patrones de diseño, persistencia de datos y construcción de interfaces gráficas de escritorio.

---

## Contenido

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Ejecución](#ejecución)
- [Rol y permisos](#rol-y-permisos)
- [Backlog y flujo de ramas](#backlog-y-flujo-de-ramas)
- [Créditos](#créditos)

## Características

- **Autenticación**: inicio de sesión con contraseña cifrada en **SHA-256** y registro de nuevos usuarios.
- **Gestión de sesión**: usuario actual disponible durante toda la sesión mediante `SesionContext`; cierre de sesión desde el panel.
- **Panel de administración**: barra lateral y tarjetas de acceso rápido a cada módulo, con bienvenida personalizada según el rol.
- **CRUD completo** de: usuarios, libros, autores, categorías, editoriales, clientes y relación autor–libro.
- **Módulo comercial**: registro de ventas, detalle de venta por líneas de artículo y listado de ventas.
- **Inventario**: consulta de existencias/stock por libro.
- **Validaciones**: mensajes de error y alertas uniformes vía `ValidacionException`.
- **Persistencia**: base de datos MySQL accedida mediante procedimientos almacenados (SP).

## Tecnologías

| Capa | Tecnología |
|------|------------|
| Lenguaje | Java (JavaFX) |
| Interfaz gráfica | JavaFX con vistas FXML y hojas de estilo CSS |
| Base de datos | MySQL (procedimientos almacenados) |
| Conector | MySQL Connector/J (`com.mysql.cj.jdbc.Driver`) |
| IDE sugerido | Apache NetBeans |

## Arquitectura

El proyecto sigue una arquitectura por capas que separa responsabilidades:

```
┌─────────────────────────────────────────┐
│  Vista (FXML + Controladores)            │
│  src/org/ac/view · src/org/ac/controller │
├─────────────────────────────────────────┤
│  Lógica / Gestión de sesión              │
│  src/org/ac/system · src/org/ac/manager  │
├─────────────────────────────────────────┤
│  Acceso a datos (DAO)                    │
│  src/org/ac/dao · src/org/ac/dao/impl    │
├─────────────────────────────────────────┤
│  Modelo de entidades                     │
│  src/org/ac/model                        │
├─────────────────────────────────────────┤
│  Base de datos MySQL (tablas y SPs)      │
└─────────────────────────────────────────┘
```

- **Modelo**: clases de entidad (`Usuario`, `Libro`, `Autor`, `Venta`, etc.).
- **DAO**: interfaces (`Dao`, `Crud`, `UsuarioDAO`, …) e implementaciones en `dao/impl` que invocan los procedimientos almacenados.
- **Controladores**: lógica de las vistas JavaFX (eventos, tablas, validaciones y navegación).
- **Singleton**: `Conexion` (conexión a BD) y `SesionContext` (usuario autenticado).

## Estructura del proyecto

```
LibraryApp/
├── src/
│   ├── db.properties            # Credenciales locales (NO versionado)
│   └── org/ac/
│       ├── controller/          # Controladores JavaFX de cada vista
│       ├── dao/                 # Interfaces de acceso a datos
│       │   └── impl/            # Implementaciones con MySQL
│       ├── exception/           # ValidacionException (validaciones centralizadas)
│       ├── manager/             # SesionContext (sesión del usuario)
│       ├── model/               # Entidades del dominio
│       ├── system/              # Principal (punto de entrada y navegación)
│       ├── util/                # Conexion, SecurityUtil (hash SHA-256)
│       └── view/
│           ├── fxml/            # Vistas FXML
│           └── style/           # Hojas de estilo CSS
├── test/                        # Pruebas
├── nbproject/                   # Configuración de NetBeans
├── db.properties.example        # Plantilla de credenciales (versionada)
└── build.xml                    # Script Ant
```

## Requisitos

- **JDK** 11 o superior (compatible con JavaFX).
- **JavaFX SDK** en el classpath.
- **MySQL** 5.7 o superior.
- **Connector/J** (`mysql-connector-java`) agregado al proyecto.

## Configuración de la base de datos

La conexión se lee desde `src/db.properties` (recurso copiado al classpath en la compilación):

```properties
db.url=jdbc:mysql://localhost:3306/libreriadb_in4cm?serverTimezone=UTC
db.user=TU_USUARIO
db.password=TU_CONTRASEÑA
```

Para configurar tu entorno:

1. Copia `db.properties.example` como `src/db.properties`.
2. Ajusta los valores (URL, usuario y contraseña de tu MySQL).
3. Recompila para que el archivo se copie a `build/classes`.

> `src/db.properties` está en `.gitignore` y **no se versiona**: no subas credenciales reales al repositorio. Si el archivo falta o está incompleto, `Conexion` lanza un error claro al arrancar.

La base de datos `libreriadb_in4cm` debe contener las tablas y procedimientos almacenados (`sp_iniciar_sesion`, `sp_crear_usuario`, `sp_*` de cada módulo) que invocan los DAO. El plan de estructura y datos se puede reproducir siguiendo el backlog (tarea 3 de `BACKLOG.md`).

## Ejecución

Desde Apache NetBeans:

1. Abrir el proyecto (`File > Open Project`).
2. Verificar que JavaFX y el conector MySQL estén en el classpath.
3. Configurar la base de datos y credenciales.
4. Ejecutar la clase principal `org.ac.system.Principal`.

## Rol y permisos

El sistema distingue dos roles:

| Rol | Acceso |
|-----|--------|
| `admin` | Panel de administración completo |
| `empleado` | Panel de administración (mismo dashboard en esta versión) |

## Backlog y flujo de ramas

Consulta [`BACKLOG.md`](BACKLOG.md) para la lista de tareas (backlog) que documenta la construcción del proyecto por funcionalidades, cada una en su propia rama, con sus criterios de aceptación y dependencias.

## Créditos

Proyecto educativo desarrollado para el curso de **Programación 1** (estudiantes de informática). Los alumnos demuestran aquí competencias en: programación orientada a objetos, persistencia de datos con JDBC y procedimientos almacenados, construcción de interfaces con JavaFX y trabajo colaborativo con Git.
