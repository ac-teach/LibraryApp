# Plan de Proyecto Ágil (Scrum)
## Sistema de Gestión para Librería "Página Viva"
**Tecnología:** Java 21 · JavaFX 21 (FXML) · MySQL · NetBeans
**Duración total simulada:** 4 Sprints de 2 semanas (8 semanas)

---

## 1. Documento de Visión del Producto

### 1.1 Declaración de Visión

> **Para** los dueños y el personal de librerías físicas pequeñas y medianas,
> **que** necesitan controlar ventas e inventario de forma integrada y confiable,
> **el Sistema de Gestión Librería "Página Viva" (SGL)**
> **es** una aplicación de escritorio,
> **que** centraliza los procesos de caja, inventario y administración mediante accesos diferenciados por rol.
> **A diferencia de** llevar registros en hojas de cálculo o cuadernos separados,
> **nuestro producto** unifica la información en una sola base de datos, reduce errores manuales y entrega información en tiempo real para la toma de decisiones.

### 1.2 Problema a resolver
La librería "Página Viva" gestiona sus ventas e inventario de forma manual y desarticulada, lo que genera: descuadres de caja, desconocimiento del stock real, pérdida de ventas por falta de libros disponibles y ausencia de reportes para la gerencia.

### 1.3 Objetivos del producto
| # | Objetivo | Métrica de éxito |
|---|----------|-------------------|
| 1 | Digitalizar el proceso de venta en caja | Registro de venta en menos de 1 minuto |
| 2 | Controlar el inventario en tiempo real | Stock actualizado automáticamente tras cada venta/ingreso |
| 3 | Diferenciar el acceso según el rol del usuario | 3 Dashboards funcionales (Cajero, Bodega, Admin) |
| 4 | Entregar información gerencial | Reportes de ventas e inventario disponibles bajo demanda |

### 1.4 Alcance
**Dentro del alcance:** autenticación con roles, módulo de caja, módulo de bodega/inventario, módulo de administración, dashboards diferenciados, reportes básicos.
**Fuera del alcance:** integración con pasarelas de pago electrónico, facturación electrónica oficial, app móvil, comercio electrónico (venta en línea).

### 1.5 Stakeholders (simulados)
| Rol | Interés |
|-----|---------|
| Gerencia de la librería (Patrocinador) | Visibilidad del negocio y control financiero |
| Cajeros | Herramienta ágil para atender ventas |
| Encargados de bodega | Control preciso del inventario |
| Docente/Facilitador del curso | Evaluar la correcta aplicación de Scrum |
| Equipo de desarrollo (estudiantes) | Construir el producto e implementar el marco Scrum |

---

## 2. Acta de Constitución del Proyecto (Project Charter)

| Campo | Detalle |
|---|---|
| **Nombre del proyecto** | Sistema de Gestión para Librería "Página Viva" (SGL) |
| **Patrocinador** | Gerencia General de Librería "Página Viva" (simulado) |
| **Product Owner** | Asignado por el equipo/docente |
| **Scrum Master** | Asignado por el equipo/docente |
| **Equipo de Desarrollo** | Estudiantes del curso |
| **Justificación** | Reemplazar procesos manuales propensos a error por un sistema centralizado y auditable |
| **Duración estimada** | 8 semanas (4 sprints de 2 semanas) |
| **Restricciones** | Stack tecnológico fijo (Java 21, JavaFX 21 FXML, MySQL, NetBeans); equipo de estudiantes con dedicación parcial |
| **Supuestos** | Se cuenta con acceso a NetBeans y MySQL desde el inicio; el docente actúa como Product Owner o valida sus decisiones |
| **Criterios de éxito** | Los 3 Dashboards funcionan según el rol; el ciclo venta→descuento de stock es correcto; el equipo demuestra el uso correcto de artefactos y eventos Scrum |

### 2.1 Riesgos iniciales
| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Baja experiencia del equipo en JavaFX/FXML | Alta | Media | Capacitación inicial (Sprint 0) y pair programming |
| Cambios de alcance a mitad de curso | Media | Alta | Backlog priorizado y control de cambios vía Product Owner |
| Problemas de concurrencia en la base de datos (varios roles a la vez) | Media | Media | Pruebas de integración desde el Sprint 2 |
| Subestimación de historias | Alta | Media | Ajuste de velocity en cada Sprint Planning |

---

## 3. Roles del Equipo Scrum
*(Roles genéricos — a asignar por el docente/equipo)*

| Rol Scrum | Responsabilidad principal |
|---|---|
| **Product Owner** | Gestiona y prioriza el Product Backlog, representa la voz del negocio |
| **Scrum Master** | Facilita las ceremonias, elimina impedimentos, vela por el cumplimiento del marco |
| **Equipo de Desarrollo** | Diseña, construye y prueba el incremento del producto cada sprint |

> Nota: no confundir los **roles Scrum** (arriba) con los **roles del sistema** (Cajero, Bodega, Admin), que son perfiles de usuario final dentro de la aplicación.

### 3.1 Personas / Roles de usuario del sistema
| Rol del sistema | Necesidad principal | Dashboard esperado |
|---|---|---|
| **Cajero** | Vender rápido y sin errores | Búsqueda de libros, registro de venta, ticket, resumen del turno |
| **Bodega** | Mantener el stock correcto | Ingreso/salida de inventario, alertas de stock bajo, ficha de libros |
| **Admin** | Visión global y control | Gestión de usuarios, reportes de ventas/inventario, catálogo, KPIs |

---

## 4. Definition of Ready (DoR)
Una historia de usuario se considera **lista para entrar a un sprint** cuando:
- Tiene criterios de aceptación claros y verificables.
- Fue estimada en Story Points por el equipo.
- No tiene dependencias bloqueantes conocidas.
- El Product Owner definió su prioridad.
- El equipo entiende el valor de negocio que aporta.

## 5. Definition of Done (DoD)
Una historia de usuario se considera **terminada** cuando:
1. El código está integrado en la rama principal del repositorio.
2. Compila y ejecuta sin errores en NetBeans.
3. La interfaz FXML fue validada visualmente contra el diseño acordado.
4. Se realizaron pruebas manuales del flujo (caso feliz + al menos un caso de error).
5. Pasó una revisión de código por otro integrante del equipo.
6. No introduce errores críticos ni bloqueantes conocidos.
7. Fue demostrada y aceptada por el Product Owner en la Sprint Review.

---

## 6. Product Backlog

Prioridad según MoSCoW: **M**ust / **S**hould / **C**ould / **W**on't (este ciclo)

### Épica 1 — Autenticación y Gestión de Roles
| ID | Historia de Usuario | Prioridad | Puntos |
|---|---|---|---|
| US-1.1 | Como usuario del sistema, quiero iniciar sesión con usuario y contraseña, para acceder de forma segura. | Must | 5 |
| US-1.2 | Como administrador, quiero crear, editar y desactivar cuentas de usuario, para gestionar el acceso del personal. | Must | 8 |
| US-1.3 | Como sistema, quiero redirigir a cada usuario a su Dashboard según su rol, para mostrar solo lo que le corresponde. | Must | 5 |
| US-1.4 | Como usuario, quiero recuperar/cambiar mi contraseña, para mantener segura mi cuenta. | Should | 3 |

### Épica 2 — Módulo de Caja (Ventas)
| ID | Historia de Usuario | Prioridad | Puntos |
|---|---|---|---|
| US-2.1 | Como cajero, quiero buscar libros por título, ISBN o autor, para agregarlos rápido a una venta. | Must | 5 |
| US-2.2 | Como cajero, quiero registrar una venta con varios libros y calcular el total automáticamente. | Must | 8 |
| US-2.3 | Como cajero, quiero aplicar descuentos autorizados a una venta, para promociones vigentes. | Should | 5 |
| US-2.4 | Como cajero, quiero generar e imprimir un comprobante de venta, para entregarlo al cliente. | Must | 5 |
| US-2.5 | Como cajero, quiero anular o registrar la devolución de una venta reciente. | Should | 5 |
| US-2.6 | Como cajero, quiero ver un resumen de mis ventas del día en mi Dashboard. | Could | 3 |

### Épica 3 — Módulo de Bodega (Inventario)
| ID | Historia de Usuario | Prioridad | Puntos |
|---|---|---|---|
| US-3.1 | Como encargado de bodega, quiero registrar el ingreso de nuevos libros al inventario. | Must | 5 |
| US-3.2 | Como encargado de bodega, quiero registrar salidas de inventario (mermas, traslados, devoluciones a proveedor). | Must | 5 |
| US-3.3 | Como encargado de bodega, quiero recibir alertas de libros con stock bajo el mínimo. | Must | 5 |
| US-3.4 | Como encargado de bodega, quiero registrar y editar la ficha de cada libro (título, autor, ISBN, categoría, precio, stock mínimo). | Must | 8 |
| US-3.5 | Como encargado de bodega, quiero ver en mi Dashboard el listado de libros con stock crítico. | Should | 3 |

### Épica 4 — Módulo de Administración y Reportes
| ID | Historia de Usuario | Prioridad | Puntos |
|---|---|---|---|
| US-4.1 | Como administrador, quiero un Dashboard general con ventas totales, inventario y usuarios activos. | Must | 8 |
| US-4.2 | Como administrador, quiero generar reportes de ventas por período (día/semana/mes). | Must | 5 |
| US-4.3 | Como administrador, quiero reportes de inventario (más vendidos, stock valorizado). | Should | 5 |
| US-4.4 | Como administrador, quiero gestionar categorías, proveedores y precios de libros. | Should | 5 |
| US-4.5 | Como administrador, quiero exportar reportes en PDF o Excel. | Could | 5 |

**Total del Product Backlog: 20 historias · 93 puntos estimados**

---

## 7. Ejemplo de Historia de Usuario Detallada (formato para el resto)

**US-2.2 — Registrar una venta**
> Como **cajero**, quiero **registrar una venta con múltiples libros y ver el total calculado automáticamente**, para **cobrar correctamente al cliente sin errores manuales**.

**Criterios de aceptación (Gherkin):**
- **Dado** que el cajero agregó uno o más libros a la venta, **cuando** confirma la venta, **entonces** el sistema calcula el total sumando precio × cantidad de cada ítem.
- **Dado** que un libro no tiene stock suficiente, **cuando** el cajero intenta agregarlo, **entonces** el sistema muestra una advertencia y no permite continuar.
- **Dado** que la venta fue confirmada, **cuando** se guarda, **entonces** el stock de cada libro vendido se descuenta automáticamente en la base de datos.

**Puntos:** 8 · **Prioridad:** Must

---

## 8. Sprint Planning (4 Sprints · 2 semanas c/u)

### Sprint 0 (opcional, previo) — Preparación
Configuración del entorno (NetBeans, conexión JDBC a MySQL), diseño de la base de datos, wireframes de los 3 Dashboards, definición del Product Backlog inicial. *(No entrega incremento funcional al usuario.)*

### Sprint 1 — "Acceso y Cimientos"
**Objetivo del sprint:** Un usuario puede iniciar sesión y ser dirigido a un Dashboard vacío según su rol.
| Historias incluidas | Puntos |
|---|---|
| US-1.1, US-1.2, US-1.3, US-1.4 | 21 |

### Sprint 2 — "Vender sin fricción"
**Objetivo del sprint:** El cajero puede completar el ciclo completo de una venta y emitir su comprobante.
| Historias incluidas | Puntos |
|---|---|
| US-2.1, US-2.2, US-2.4, US-2.6 | 21 |

### Sprint 3 — "Inventario bajo control"
**Objetivo del sprint:** Bodega puede mantener el catálogo y el stock actualizado, con alertas automáticas.
| Historias incluidas | Puntos |
|---|---|
| US-3.1, US-3.2, US-3.3, US-3.4, US-3.5 | 26 |

### Sprint 4 — "Visión gerencial y cierre"
**Objetivo del sprint:** El administrador cuenta con panel de control, reportes y gestión de catálogo/usuarios; se cierran pendientes de sprints anteriores (US-2.3, US-2.5).
| Historias incluidas | Puntos |
|---|---|
| US-4.1, US-4.2, US-4.3, US-4.4, US-2.3, US-2.5 | 33 |

> *Nota pedagógica: la carga de puntos crece hacia el final a propósito, para que el equipo experimente el ajuste de la velocity real vs. planificada entre sprints y practique la re-priorización con el Product Owner.*

---

## 9. Resumen de Cadencia Scrum sugerida
| Evento | Frecuencia | Duración sugerida |
|---|---|---|
| Sprint Planning | Inicio de cada sprint | 1 hora |
| Daily Scrum | Diaria (o por sesión de clase) | 15 min |
| Sprint Review | Fin de cada sprint | 30–45 min |
| Sprint Retrospective | Fin de cada sprint | 30 min |

---

*Documento generado como material de apoyo docente para la enseñanza de Scrum aplicado a un proyecto simulado de desarrollo de software.*
