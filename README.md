# Food Store — Sistema de Consola en Java 21

## Requisitos previos
- Java JDK 21
- XAMPP con MySQL en puerto 3306 (usuario: `root`, contraseña: vacía)
- Maven 3.8+

## Pasos para ejecutar

1. Iniciar MySQL desde XAMPP.
2. Ejecutar `schema.sql` en phpMyAdmin o por consola:
```bash
   mysql -u root < schema.sql
```
3. Compilar y ejecutar:
```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="com.foodstore.Main"
```
## Estructura
src/main/java/com/foodstore/
├── config/      → ConexionDB
├── dao/         → Interfaces e implementaciones JDBC
├── entities/    → Modelo (Base, Categoria, Producto, Usuario, Pedido, DetallePedido)
├── enums/       → Rol, Estado, FormaPago
├── exception/   → EntidadNoEncontradaException, ReglaNegocioException
├── service/     → Lógica de negocio
└── Main.java    → Punto de entrada y menú de consola
