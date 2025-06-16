package com.Perfulandia.ApiInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Perfulandia.ApiInventario.Model.Sucursal;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer>{

}
