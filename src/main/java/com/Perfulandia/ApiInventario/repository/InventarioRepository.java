package com.Perfulandia.ApiInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Perfulandia.ApiInventario.Model.Inventario;
import com.Perfulandia.ApiInventario.Model.Producto;
import com.Perfulandia.ApiInventario.Model.Sucursal;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    // Método para buscar si ya existe un registro para un producto en una sucursal
    Optional<Inventario> findByProductoAndSucursal(Producto producto, Sucursal sucursal);

    // Método para obtener todo el inventario de una sucursal
    List<Inventario> findBySucursal(Sucursal sucursal);
}