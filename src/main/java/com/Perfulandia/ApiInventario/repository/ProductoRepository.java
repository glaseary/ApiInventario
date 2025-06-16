package com.Perfulandia.ApiInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Perfulandia.ApiInventario.Model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

}
