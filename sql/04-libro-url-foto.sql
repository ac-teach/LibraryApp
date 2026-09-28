-- =============================================================================
-- Migración: soporte de url_foto en los procedimientos almacenados de LIBROS
-- Requisito: la columna url_foto VARCHAR(100) ya debe existir en la tabla libros
-- (ver sql/03-update-schema-data.sql: ALTER TABLE libros ADD COLUMN url_foto).
-- =============================================================================
use libreriadb_in4cm;

ALTER TABLE libros
    ADD COLUMN url_foto VARCHAR(100);

drop procedure if exists sp_listar_todos_libros;
drop procedure if exists sp_buscar_libro_id;
drop procedure if exists sp_crear_libro;
drop procedure if exists sp_actualizar_libro;

delimiter $$
create procedure sp_listar_todos_libros()
begin
    select isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock, url_foto from libros;
end $$

create procedure sp_buscar_libro_id(in _isbn varchar(20))
begin
    select isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock, url_foto
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
    in _stock int,
    in _url_foto varchar(100))
begin
    insert into libros(isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, stock, url_foto)
    values (_isbn, _titulo, _fecha_publicacion, _precio, _id_categoria, _nit_editorial, _stock, _url_foto);
end $$

create procedure sp_actualizar_libro(
    in _isbn varchar(20),
    in _titulo varchar(100),
    in _fecha_publicacion date,
    in _precio decimal(8,2),
    in _id_categoria int,
    in _nit_editorial varchar(20),
    in _stock int,
    in _url_foto varchar(100))
begin
    update libros
    set titulo = _titulo,
        fecha_publicacion = _fecha_publicacion,
        precio = _precio,
        id_categoria = _id_categoria,
        nit_editorial = _nit_editorial,
        stock = _stock,
        url_foto = _url_foto
    where isbn = _isbn;
end $$
delimiter ;