CREATE DATABASE IF NOT EXISTS food_store;
USE food_store;

CREATE TABLE categorias (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    eliminado   BOOLEAN  DEFAULT FALSE,
    createdAt   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE productos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  VARCHAR(255),
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT NOT NULL DEFAULT 0,
    imagen       VARCHAR(255),
    disponible   BOOLEAN DEFAULT TRUE,
    categoria_id BIGINT NOT NULL,
    eliminado    BOOLEAN  DEFAULT FALSE,
    createdAt    DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    apellido  VARCHAR(150) NOT NULL,
    mail      VARCHAR(150) NOT NULL UNIQUE,
    celular   VARCHAR(20),
    password  VARCHAR(255) NOT NULL,
    rol       ENUM('ADMIN','USUARIO') NOT NULL DEFAULT 'USUARIO',
    eliminado BOOLEAN  DEFAULT FALSE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE pedidos (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT NOT NULL,
    fecha       DATE NOT NULL,
    estado      ENUM('PENDIENTE','CONFIRMADO','TERMINADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    formaPago   ENUM('TARJETA','TRANSFERENCIA','EFECTIVO') NOT NULL,
    eliminado   BOOLEAN  DEFAULT FALSE,
    createdAt   DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE detalles_pedido (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id      BIGINT NOT NULL,
    producto_id    BIGINT NOT NULL,
    cantidad       INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subtotal       DECIMAL(10,2) NOT NULL,
    eliminado      BOOLEAN  DEFAULT FALSE,
    createdAt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;