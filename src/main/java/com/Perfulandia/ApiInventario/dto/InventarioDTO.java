package com.Perfulandia.ApiInventario.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class InventarioDTO {
    private Integer idInventario;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer stockActual;
    private LocalDate fechaActualizacion;
    private ProductoDTO producto;
    private SucursalDTO sucursal;
}