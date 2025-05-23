-- Crear tabla Autor
CREATE TABLE Autor (
    id_autor NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    nacionalidad VARCHAR2(50)
);

-- Crear tabla Usuario
CREATE TABLE Usuario (
    id_usuario NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    correo VARCHAR2(100) UNIQUE NOT NULL,
    contraseña VARCHAR2(100) NOT NULL
);

-- Crear tabla Libro
CREATE TABLE Libro (
    id_libro NUMBER PRIMARY KEY,
    titulo VARCHAR2(200) NOT NULL,
    isbn VARCHAR2(20) UNIQUE,
    categoria VARCHAR2(100),
    estado VARCHAR2(50),
    id_autor NUMBER NOT NULL,
    CONSTRAINT fk_libro_autor FOREIGN KEY (id_autor) REFERENCES Autor(id_autor)
);

-- Crear tabla Prestamo
CREATE TABLE Prestamo (
    id_prestamo NUMBER PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    id_libro NUMBER NOT NULL,
    id_usuario NUMBER NOT NULL,
    CONSTRAINT fk_prestamo_libro FOREIGN KEY (id_libro) REFERENCES Libro(id_libro),
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Agregar campo 'rol' a Usuario
ALTER TABLE Usuario ADD rol VARCHAR2(20) DEFAULT 'usuario' CHECK (rol IN ('admin', 'usuario'));

-- Insertar usuario administrador
INSERT INTO Usuario (id_usuario, nombre, correo, contraseña, rol)
VALUES (1, 'admin', 'admin@biblioteca.com', 'admin', 'admin');

-- Insertar usuario normal
INSERT INTO Usuario (id_usuario, nombre, correo, contraseña, rol)
VALUES (2, 'usuario', 'juan.perez@gmail.com', 'usuario', 'usuario');

-- Insertar autores
INSERT INTO Autor (id_autor, nombre, nacionalidad)
VALUES (1, 'Gabriel García Márquez', 'Colombiana');

INSERT INTO Autor (id_autor, nombre, nacionalidad)
VALUES (2, 'Isabel Allende', 'Chilena');

-- Insertar libros
INSERT INTO Libro (id_libro, titulo, isbn, categoria, estado, id_autor)
VALUES (1, 'Cien Años de Soledad', '9780307474728', 'Novela', 'Disponible', 1);

INSERT INTO Libro (id_libro, titulo, isbn, categoria, estado, id_autor)
VALUES (2, 'La Casa de los Espíritus', '9781501117015', 'Ficción', 'Prestado', 2);

-- Insertar préstamo
INSERT INTO Prestamo (id_prestamo, fecha_inicio, fecha_fin, id_libro, id_usuario)
VALUES (1, TO_DATE('2025-05-01', 'YYYY-MM-DD'), TO_DATE('2025-05-10', 'YYYY-MM-DD'), 2, 2);
