package com.Perfulandia.ApiInventario.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "producto") // La tabla local de productos
@Data
public class Producto {

    @Id
    // IMPORTANTE: No es autoincremental. El ID viene del servicio original de Productos.
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(nullable = false, length = 50)
    private String nombre;

    // No necesitamos descripción, precio, costo, etc. para el inventario.
}