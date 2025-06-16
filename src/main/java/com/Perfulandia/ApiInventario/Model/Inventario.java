package com.Perfulandia.ApiInventario.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "INVENTARIO")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stock_maximo", nullable = false)
    private Integer stockMaximo;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDate fechaActualizacion;

    // Relación con la copia LOCAL de Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTO_id_producto", nullable = false)
    private Producto producto;

    // Relación con la copia LOCAL de Sucursal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUCURSAL_id_sucursal", nullable = false)
    private Sucursal sucursal;
}