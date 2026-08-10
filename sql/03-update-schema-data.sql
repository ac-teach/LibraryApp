-- tabla de usuarios
create table usuarios (
    id_usuario int auto_increment primary key,
    username varchar(50) not null unique,
    password_hash varchar(255) not null,
    rol enum('admin', 'empleado', 'cajero') not null,
    activo boolean default true,
    fecha_creacion timestamp default current_timestamp    
);

ALTER TABLE usuarios
    ADD COLUMN email VARCHAR(50) NOT NULL AFTER username,
    ADD COLUMN first_name VARCHAR(50) NOT NULL AFTER email,
    ADD COLUMN last_name VARCHAR(50) NOT NULL AFTER first_name;

-- procedimiento para registrar usuario
delimiter //
create procedure sp_registrar_usuario(
    in _username varchar(50), 
    in _password_hash varchar(255), 
    in _rol varchar(20)
)
begin
    insert into usuarios (username, password_hash, rol) 
    values (_username, _password_hash, _rol);
end //
delimiter ;

-- drop procedure sp_iniciar_sesion;
-- procedimiento para iniciar sesión
delimiter //
create procedure sp_iniciar_sesion(
    in _username varchar(50), 
    in _password_hash varchar(255)
)
begin
    select id_usuario, username, rol 
    from usuarios 
    where username = _username 
      and password_hash = _password_hash 
      and activo = true 
    limit 1;
end //
delimiter ;

call sp_registrar_usuario('alvaro',sha2('admin',256),'admin');
call sp_iniciar_sesion('alvaro',sha2('admin',256));

select * from usuarios;


use libreriadb_in4cm;

-- ============================================================================
-- PASO 1: Eliminar Llaves Foráneas (FK) que tienen ON DELETE CASCADE peligroso
-- o cuyas columnas padre van a cambiar de tipo de dato.
-- ============================================================================
alter table libros 
    drop foreign key fk_a_categorias,
    drop foreign key fk_a_editoriales;

alter table compras 
    drop foreign key fk_a_cliente;

alter table detalle_compra 
    drop foreign key fk_a_libros;

-- ============================================================================
-- PASO 2: Corregir Nombres y Tipos de Datos en Tablas de Catálogo y Usuarios
-- ============================================================================
-- Modificar CUI a VARCHAR(13) para conservar ceros a la izquierda
alter table clientes 
    modify column cui varchar(13);

-- Corregir error ortográfico en la columna de editoriales
alter table editoriales 
    rename column direccion_editoria to direccion_editorial;

-- Renombrar 'id' a 'id_usuario' para mantener la convención de nombres
alter table usuarios 
    rename column id to id_usuario;

-- ============================================================================
-- PASO 3: Aplicar Cambios de Inventario y Precios en 'libros'
-- ============================================================================
alter table libros 
    add column stock int not null default 0,
    add column stock_minimo int not null default 5,
    modify column precio decimal(10,2) not null;

-- ============================================================================
-- PASO 4: Vincular Ventas con Usuarios y Ajustar Tipo de CUI en 'compras'
-- ============================================================================
alter table compras 
    modify column cui_cliente varchar(13),
    modify column total_compra decimal(10,2) not null default 0.00,
    add column id_usuario int not null;

-- ============================================================================
-- PASO 5: Agregar Cantidad y Precio Unitario a 'detalle_compra'
-- ============================================================================
alter table detalle_compra 
    add column cantidad int not null default 1,
    add column precio_unitario decimal(10,2) not null default 0.00;


-- =============================================================================
-- CAMBIO DE NOMBRE de entidad compras A ventas y detalle compras a detalle_ventas
-- =============================================================================
RENAME TABLE compras TO ventas, detalle_compra TO detalle_venta;

ALTER TABLE ventas 
rename column no_compra to no_venta, 
rename column fecha_compra to fecha_venta,
rename column total_compra to total_venta;
ALTER TABLE detalle_venta 
rename column id_detalle_compra to id_detalle_venta, 
rename column no_compra to no_venta;

-- ============================================================================
-- Recrear las Llaves Foráneas con ON DELETE RESTRICT
-- ============================================================================


alter table libros 
    add constraint fk_libros_categoria foreign key (id_categoria) references categorias(id_categoria) on delete restrict,
    add constraint fk_libros_editorial foreign key (nit_editorial) references editoriales(nit) on delete restrict;

-- Relaciones de Compras (Ventas)
alter table ventas
    add constraint fk_ventas_cliente foreign key (cui_cliente) references clientes(cui) on delete restrict,
    add constraint fk_ventas_usuario foreign key (id_usuario) references usuarios(id_usuario) on delete restrict;
    

-- Relación de Detalle
alter table detalle_venta
    add constraint fk_dc_libro foreign key (isbn) references libros(isbn) on delete restrict;
--

-- =============================================================================
-- Edicion de sp para ventas eliminamos al inicio compras
-- =============================================================================
drop procedure if exists sp_insertarcompra;
drop procedure if exists sp_listarcompras;
drop procedure if exists sp_buscarcompra;
drop procedure if exists sp_actualizarcompra;
drop procedure if exists sp_eliminarcompra;

drop procedure if exists sp_insertar_venta;

delimiter $$

create procedure sp_listar_ventas()
begin
    select no_venta, fecha_venta, total_venta, cui_cliente, id_usuario from ventas;
end $$
create procedure sp_buscar_venta(in _no int)
begin
    select no_venta, fecha_venta, total_venta, cui_cliente, id_usuario 
    from ventas where no_venta = _no;
end $$


create procedure sp_insertar_venta(
	in _total decimal(8,2),
    in _cui varchar(13),
    in _id_usuario int)
begin
    insert into ventas(total_venta, cui_cliente, id_usuario)
    values (_total, _cui, _id_usuario);
end $$

create procedure sp_actualizar_venta(
	in _no int, 
    in _total decimal(8,2), 
    in _cui bigint)
begin
    update ventas 
    set total_venta = _total, cui_cliente = _cui
    where no_venta = _no;
end $$

create procedure sp_eliminar_venta(in _no int)
begin
    delete from ventas where no_venta = _no;
end $$

delimiter ;
    

-- =============================================================================
-- Edicion de sp para detalle_ventas eliminamos al inicio detalle_compras
-- =============================================================================
drop procedure if exists sp_insertardetallecompra;
drop procedure if exists sp_listardetallecompra;
drop procedure if exists sp_buscardetallecompra;
drop procedure if exists sp_actualizardetallecompra;
drop procedure if exists sp_eliminardetallecompra;

delimiter $$

create procedure sp_listar_detalle_venta()
begin
    select id_detalle_venta, no_venta, isbn, cantidad, precio_unitario as precio 
    from detalle_venta;
end $$

create procedure sp_insertar_detalle_venta(
	in _no int, 
    in _isbn varchar(20),
    in _cantidad int,
    in _precio_unidad double (10,2))
begin
    insert into detalle_venta(no_venta, isbn, cantidad, precio_unitario) 
    values (_no, _isbn, _cantidad, _precio_unidad);
end $$

create procedure sp_buscar_detalle_venta(in _id int)
begin
    select id_detalle_venta, no_venta, isbn, cantidad, precio_unitario as precio 
    from detalle_venta where id_detalle_venta = _id;
end $$

create procedure sp_actualizar_detalle_venta(
    in _id_detalle_venta int,
    in _no_venta int,
    in _isbn varchar(20),
    in _cantidad int,
    in _precio double(10,2)
	)
begin
    update detalle_venta 
    set no_venta = _no_venta, isbn = _isbn, cantidad = _cantidad, precio_unitario = _precio
    where id_detalle_venta = _id_detalle_venta;
end $$

create procedure sp_eliminar_detalle_venta(in _id_detalle_venta int)
begin
    delete from detalle_venta where id_detalle_venta = _id_detalle_venta;
end $$

delimiter ;

-- -----

-- =============================================================================
-- 5. crud: libros
-- =============================================================================
drop procedure if exists sp_insertarlibro;
drop procedure if exists sp_listarlibros;
drop procedure if exists sp_buscarlibro;
drop procedure if exists sp_actualizarlibro;
drop procedure if exists sp_buscar_libro_id;


delimiter $$
create procedure sp_listar_todos_libros()
begin
    select isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock from libros;
end $$

create procedure sp_buscar_libro_id(in _isbn varchar(20))
begin
    select isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock 
    from libros 
    where isbn = _isbn;
end $$

create procedure sp_crear_libro(
    in _isbn varchar(20),
    in _titulo varchar(100),
    in _fecha_publicacion date,
    in _precio decimal(8,2),
    in _id_categoria int,
    in _nit_editorial varchar(20),
    in _stock int)
begin
    insert into libros(isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock) 
    values (_isbn, _titulo, _fecha_publicacion, _precio, _id_categoria, _nit_editorial, _stock);
end $$

create procedure sp_actualizar_libro(
    in _isbn varchar(20),
    in _titulo varchar(100),
    in _fecha_publicacion date,
    in _precio decimal(8,2),
    in _id_categoria int,
    in _nit_editorial varchar(20),
    in _stock int)
begin
    update libros 
    set titulo = _titulo, 
        fecha_publicacion = _fecha_publicacion, 
        precio = _precio, 
        id_categoria = _id_categoria, 
        nit_editorial = _nit_editorial,
        stock = _stock
    where isbn = _isbn;
end $$

create procedure sp_eliminar_libro(in _isbn varchar(20))
begin
    delete from libros where isbn = _isbn;
end $$

delimiter ;


-- =============================================================================
-- 2. crud: editoriales
-- =============================================================================
drop procedure if exists sp_insertar_editorial;
drop procedure if exists sp_listareditoriales;
drop procedure if exists sp_buscareditorial;
drop procedure if exists sp_actualizareditorial;
drop procedure if exists sp_eliminareditorial;


delimiter $$

create procedure sp_crear_editorial(
    in _nit varchar(20),
    in _nombre_editorial varchar(100),
    in _telefono_editorial varchar(15),
    in _direccion_editorial varchar(100)
)
begin
    insert into editoriales(nit, nombre_editorial, telefono_editorial, direccion_editorial) 
    values (_nit, _nombre_editorial, _telefono_editorial, _direccion_editorial);
end $$

create procedure sp_listar_todos_editoriales()
begin
    select nit, nombre_editorial, telefono_editorial, direccion_editorial from editoriales;
end $$

create procedure sp_buscar_editorial_por_id(in _nit varchar(20))
begin
    select nit, nombre_editorial, telefono_editorial, direccion_editorial
    from editoriales 
    where nit = _nit;
end $$

create procedure sp_actualizar_editorial(
    in _nit varchar(20),
    in _nombre_editorial varchar(100),
    in _telefono_editorial varchar(15),
    in _direccion_editorial varchar(100)
)
begin
    update editoriales 
    set nombre_editorial = _nombre_editorial,
        telefono_editorial = _telefono_editorial,
        direccion_editorial = _direccion_editorial
    where nit = _nit;
end $$

create procedure sp_eliminar_editorial(in _nit varchar(20))
begin
    delete from editoriales where nit = _nit;
end $$

delimiter ;

-- ============================================================================
-- SP PARA USUARIO
-- ============================================================================
delimiter $$
create procedure sp_crear_usuario(
    in _username varchar(50),
    in _email varchar(50),
    in _first_name varchar(50),
    in _last_name varchar(50),
    in _password_hash varchar(255),
    in _rol enum('admin', 'empleado', 'cajero')
)
begin
    insert into usuarios (username, email, first_name, last_name, password_hash, rol)
    values (_username, _email, _first_name, _last_name, _password_hash, _rol);
end $$

create procedure sp_listar_todos_usuarios()
begin
    select 
        id_usuario, 
        username, 
        email, 
        first_name, 
        last_name, 
        rol, 
        activo, 
        fecha_creacion
    from usuarios;
end $$

create procedure sp_obtener_usuario_por_id(in _id_usuario int)
begin
    select 
        id_usuario, 
        username, 
        email, 
        first_name, 
        last_name, 
        rol, 
        activo, 
        fecha_creacion
    from usuarios
    where id_usuario = _id_usuario;
end $$

create procedure sp_actualizar_usuario(
    in _id_usuario int,
    in _username varchar(50),
    in _email varchar(50),
    in _first_name varchar(50),
    in _last_name varchar(50),
    in _rol enum('admin', 'empleado', 'cajero'),
    in _activo boolean
)
begin
    update usuarios
    set username = _username,
        email = _email,
        first_name = _first_name,
        last_name = _last_name,
        rol = _rol,
        activo = _activo
    where id_usuario = _id_usuario;
end $$

create procedure sp_cambiar_password(in _id_usuario int, in _password_hash varchar(255)
)
begin
    update usuarios
    set password_hash = _password_hash
    where id_usuario = _id_usuario;
end $$

create procedure sp_desactivar_usuario(in _id_usuario int)
begin
    update usuarios
    set activo = false
    where id_usuario = _id_usuario;
end $$

-- eliminación física (física y definitiva)
create procedure sp_eliminar_usuario(in _id_usuario int)
begin
    delete from usuarios
    where id_usuario = _id_usuario;
end $$

delimiter ;
