package com.Perfulandia.ApiInventario.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
public class InventarioDTO extends RepresentationModel<InventarioDTO> {
    private Integer idInventario;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer stockActual;
    private LocalDate fechaActualizacion;
    private ProductoDTO producto;
    private SucursalDTO sucursal;
}