
CREATE DATABASE IF NOT EXISTS tienda_ropa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

/* Selecciona la base de datos de trabajo. */

USE tienda_ropa;



/* Almacena los clientes asociados a las ventas. */
CREATE TABLE IF NOT EXISTS Cliente (
    /* Cedula unica de cada cliente; identifica cada registro. */
      cedula VARCHAR(20) PRIMARY KEY,

    /* Nombre obligatorio para identificar al cliente. */
      nombre_completo VARCHAR(100) NOT NULL


) ENGINE=InnoDB;


/* Almacena las cuentas de acceso a la aplicacion. */
CREATE TABLE IF NOT EXISTS Usuario (
    /* Identifica de forma unica a cada usuario. */
    username VARCHAR(50) PRIMARY KEY,

    /* Guarda el hash de la contrasena, no el texto original. */
    password_hash VARCHAR(255) NOT NULL,

    /* Define el rol necesario para controlar los permisos. */
    rol VARCHAR(20) NOT NULL

) ENGINE=InnoDB;

/* Mantiene el motor que utilizaremos para las tablas del sistema. */


/* Tabla que almacena las prendas disponibles en el catalogo de la tienda. */
CREATE TABLE IF NOT EXISTS Prenda (

    /* Identificador unico de cada prenda.
       PRIMARY KEY evita codigos repetidos y valores nulos. */
        codigo VARCHAR(20) PRIMARY KEY,

    /* Tipo de prenda (camiseta, pantalon, vestido, etc.).
       NOT NULL obliga a indicar el tipo. */
        tipo VARCHAR(50) NOT NULL,

    /* Talla de la prenda (XS, S, M, L, XL, XXL o UNICA).
       VARCHAR permite guardar texto de longitud variable. */
        talla VARCHAR(10) NOT NULL,

    /* Estado fisico de la prenda, por ejemplo NUEVO o ACEPTABLE. */
        estado VARCHAR(20) NOT NULL,

    /* Precio de venta con dos decimales para representar dinero.
       NOT NULL exige un precio y CHECK impide valores iguales o menores que cero. */
        precio DECIMAL(10,2) NOT NULL,

    /* Indica si la prenda puede venderse.
       TRUE es el valor predeterminado cuando no se indica disponibilidad. */
        disponible BOOLEAN NOT NULL DEFAULT TRUE,

    /* CHECK limita el tipo a las opciones definidas en el codigo Java. */
        CONSTRAINT chk_prenda_tipo CHECK (
        tipo IN (
                 'CAMISETA', 'CAMISA', 'PANTALON', 'VESTIDO',
                'FALDA', 'CHAQUETA', 'ABRIGO', 'SUETER',
                'BUZO', 'ZAPATOS', 'ACCESORIO'
              )
         ),

    /* Impide registrar tallas distintas de las permitidas por el proyecto. */
        CONSTRAINT chk_prenda_talla CHECK (
            talla IN ('XS', 'S', 'M', 'L', 'XL', 'XXL', 'UNICA')
         ),

    /* Evita estados que no esten contemplados en el sistema. */
         CONSTRAINT chk_prenda_estado CHECK (
            estado IN (
            'NUEVO', 'COMO_NUEVO', 'BUEN_ESTADO',
            'ACEPTABLE', 'DETERIORADO'
               )
         ),

    /* Garantiza que el precio sea positivo. */
        CONSTRAINT chk_prenda_precio CHECK (precio > 0)

    /* InnoDB permite utilizar claves foraneas y transacciones en otras tablas. */

) ENGINE=InnoDB;


/* Almacena la informacion general de cada venta realizada. */
CREATE TABLE IF NOT EXISTS Venta (

    /* Codigo unico de la factura; identifica cada venta. */
    codigo_factura VARCHAR(20) PRIMARY KEY,

    /* Fecha y hora en que se realizo la venta. */
    fecha DATETIME NOT NULL,

    /* Subtotal de la venta antes de calcular el impuesto. */
    subtotal DECIMAL(10,2) NOT NULL,

    /* Monto del impuesto IVA aplicado a la venta. */
    iva DECIMAL(10,2) NOT NULL,

    /* Importe final que debe pagar el cliente. */
    total DECIMAL(10,2) NOT NULL,

    /* Cedula del cliente que realizo la compra.
       NOT NULL obliga a asociar cada venta con un cliente. */
    cliente_cedula VARCHAR(20) NOT NULL,

    /* Relaciona la venta con un cliente registrado.
       La cedula debe existir previamente en la tabla Cliente. */
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (cliente_cedula)
        REFERENCES Cliente(cedula),

    /* Evita importes negativos en los calculos de la venta. */
    CONSTRAINT chk_venta_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_venta_iva CHECK (iva >= 0),
    CONSTRAINT chk_venta_total CHECK (total >= 0)

    /* InnoDB permite mantener la relacion con Cliente mediante la clave foranea. */

) ENGINE=InnoDB;


/* Registra las prendas incluidas en cada venta. */
CREATE TABLE IF NOT EXISTS Linea_Venta (

    /* Identificador unico de cada linea.
       AUTO_INCREMENT genera un numero automaticamente.
       PRIMARY KEY impide identificadores duplicados o nulos. */
        id INT AUTO_INCREMENT PRIMARY KEY,

    /* Codigo de la factura a la que pertenece esta linea.
       NOT NULL obliga a indicar una factura. */
        factura_codigo VARCHAR(20) NOT NULL,

    /* Codigo de la prenda que se vendio.
       NOT NULL obliga a identificar la prenda. */
        prenda_codigo VARCHAR(20) NOT NULL,

    /* Precio de la prenda al momento de la venta.
       DECIMAL permite guardar importes con dos decimales. */
        precio_congelado DECIMAL(10,2) NOT NULL,

    /* Relaciona la linea con una factura existente.
       FOREIGN KEY garantiza que la factura este registrada. */
        CONSTRAINT fk_linea_venta
            FOREIGN KEY (factura_codigo)
            REFERENCES Venta(codigo_factura),

    /* Relaciona la linea con una prenda existente. */
        CONSTRAINT fk_linea_prenda
            FOREIGN KEY (prenda_codigo)
            REFERENCES Prenda(codigo),

    /* Impide vender la misma prenda en mas de una linea.
       Cada articulo de segunda mano es unico. */
        CONSTRAINT uq_linea_prenda UNIQUE (prenda_codigo),

    /* Evita registrar precios negativos o iguales a cero. */
        CONSTRAINT chk_linea_precio CHECK (precio_congelado > 0)

    /* InnoDB permite utilizar claves foraneas y transacciones. */

) ENGINE=InnoDB;



/* =========================================
   SECCION 3: USUARIO Y PERMISOS
   ========================================= */


/* Cada integrante debe reemplazar el marcador por una
contraseña propia antes de ejecutar este comando.
No compartir ni subir contraseñas reales a Git. */

/* Crear el usuario de la aplicacion Java. */
CREATE USER IF NOT EXISTS 'tienda_app'@'localhost'
IDENTIFIED BY 'CAMBIAR_ANTES_DE_EJECUTAR';


/* Permitir consultar usuarios para el inicio de sesion. */
GRANT SELECT
      ON tienda_ropa.Usuario
          TO 'tienda_app'@'localhost';

/* Permisos para gestionar prendas. */
GRANT SELECT, INSERT, UPDATE, DELETE
      ON tienda_ropa.Prenda
          TO 'tienda_app'@'localhost';

/* Permisos para gestionar clientes. */
GRANT SELECT, INSERT, UPDATE, DELETE
      ON tienda_ropa.Cliente
          TO 'tienda_app'@'localhost';

/* Permisos para registrar y consultar ventas. */
GRANT SELECT, INSERT, UPDATE, DELETE
      ON tienda_ropa.Venta
          TO 'tienda_app'@'localhost';

/* Permisos para gestionar las lineas de cada venta. */
GRANT SELECT, INSERT, UPDATE, DELETE
      ON tienda_ropa.Linea_Venta
          TO 'tienda_app'@'localhost';

/* Verificar los permisos asignados. */
SHOW GRANTS FOR 'tienda_app'@'localhost';





