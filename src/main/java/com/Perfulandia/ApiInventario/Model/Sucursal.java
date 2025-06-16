package com.Perfulandia.ApiInventario.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sucursal")
@Data
public class Sucursal {
    
    @Id
    @Column(name = "id_sucursal")
    private Integer idSucursal;

    @Column(nullable = false, length = 40)
    private String nombreSucursal;
}   