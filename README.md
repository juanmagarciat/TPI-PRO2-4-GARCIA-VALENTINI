# Food Store — Sistema de Gestión de Tienda de Comidas

Sistema de consola desarrollado en Java con JDBC puro y MySQL. El proyecto implementa una arquitectura en capas (Entidades, DAO, Servicios) para garantizar una correcta separación de responsabilidades y cohesión.

## Tecnologías
* Java
* MySQL (XAMPP)
* JDBC Driver Tipo 4 (`mysql-connector-j-8.3.0.jar`)

## Requisitos previos
* XAMPP con Apache y MySQL corriendo (puerto 3306 por defecto).
* IntelliJ IDEA (o IDE similar) con el JDK configurado.

## Instalación y Ejecución

**1. Configurar la Base de Datos**
* Abrir el panel de control de XAMPP e iniciar los módulos **Apache** y **MySQL**.
* Ingresar a phpMyAdmin desde el navegador (`http://localhost/phpmyadmin`).
* Crear una nueva base de datos vacía.
* Importar el archivo `food_store.sql` (incluido en la raíz de este repositorio) para generar las tablas y los registros iniciales.

**2. Configurar el Proyecto en el IDE (Librería JDBC)**
* Abrir la carpeta del proyecto en IntelliJ IDEA.
* Para asegurar la correcta conexión a la base de datos, desplegar la carpeta `lib` desde el panel de archivos del proyecto.
* Hacer **clic derecho** sobre el archivo `mysql-connector-j-8.3.0.jar` y seleccionar la opción **"Add as Library..."**. *(Este paso es fundamental para vincular el driver sin necesidad de descargas externas)*.

**3. Ejecutar el Sistema**
* Navegar hasta el archivo `Main.java` ubicado dentro de la carpeta `src`.
* Ejecutar el método `main` para iniciar la aplicación por consola.

## Funcionalidades
* **Categorías:** CRUD completo (Alta, Baja lógica, Modificación y Búsqueda).
* **Productos:** CRUD completo gestionando atributos como precio, stock, imagen y disponibilidad.
* **Usuarios:** Gestión de usuarios con validación de correo electrónico único y asignación de roles (ADMIN / USUARIO).
* **Pedidos:** Creación de pedidos asociando múltiples productos, cambio de estado (Pendiente, Entregado, etc.) y eliminación con impacto en cascada.

## Informe del Proyecto
Puede consultar las decisiones de diseño, el diagrama UML y las capturas de validaciones de casos borde en el documento pdf del repositorio.
