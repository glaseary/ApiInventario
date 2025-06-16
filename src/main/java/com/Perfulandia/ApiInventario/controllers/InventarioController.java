package com.Perfulandia.ApiInventario.controllers;

import com.Perfulandia.ApiInventario.dto.InventarioDTO;
import com.Perfulandia.ApiInventario.dto.ActualizarStockDTO;
import com.Perfulandia.ApiInventario.services.InventarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    /**
     * Obtiene todo el inventario de una sucursal específica.
     * GET /api/inventario/sucursal/1
     */
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<InventarioDTO>> obtenerInventarioPorSucursal(@PathVariable Integer sucursalId) {
        try {
            return ResponseEntity.ok(inventarioService.listarInventarioPorSucursal(sucursalId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene un registro de inventario específico por su ID.
     * GET /api/inventario/5
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> obtenerInventarioPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventarioService.obtenerInventarioPorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo registro de inventario (asigna un producto a una sucursal con stock inicial).
     * POST /api/inventario
     */
    @PostMapping
    public ResponseEntity<Object> crearRegistro(@RequestBody InventarioDTO dto) {
        try {
            InventarioDTO nuevoRegistro = inventarioService.guardarRegistroInventario(dto);
            return new ResponseEntity<>(nuevoRegistro, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Error si el registro ya existe
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            // Error si el producto o sucursal no existen en las tablas locales
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Actualiza un registro de inventario completo.
     * PUT /api/inventario/5
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarRegistro(@PathVariable Integer id, @RequestBody InventarioDTO dto) {
        try {
            return ResponseEntity.ok(inventarioService.actualizarRegistroInventario(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza solo el stock de un registro.
     * PATCH /api/inventario/5/stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Object> actualizarStock(@PathVariable Integer id, @RequestBody ActualizarStockDTO request) {
        try {
            if (request.getStockActual() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El cuerpo de la petición debe contener la clave 'stockActual'."));
            }
            return ResponseEntity.ok(inventarioService.actualizarStock(id, request.getStockActual()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un registro de inventario.
     * DELETE /api/inventario/5
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(@PathVariable Integer id) {
        try {
            inventarioService.eliminarRegistroInventario(id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}