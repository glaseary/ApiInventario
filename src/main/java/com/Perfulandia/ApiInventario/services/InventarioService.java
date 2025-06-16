package com.Perfulandia.ApiInventario.services;

import com.Perfulandia.ApiInventario.Model.Inventario;
import com.Perfulandia.ApiInventario.Model.Producto;
import com.Perfulandia.ApiInventario.Model.Sucursal;
import com.Perfulandia.ApiInventario.dto.InventarioDTO;
import com.Perfulandia.ApiInventario.dto.ProductoDTO;
import com.Perfulandia.ApiInventario.dto.SucursalDTO;
import com.Perfulandia.ApiInventario.repository.InventarioRepository;
import com.Perfulandia.ApiInventario.repository.ProductoRepository;
import com.Perfulandia.ApiInventario.repository.SucursalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    // Inyectamos los repositorios de las tablas replicadas para validación
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private SucursalRepository sucursalRepository;

    // --- MÉTODOS GET ---
    public List<InventarioDTO> listarInventarioPorSucursal(Integer sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
        
        return inventarioRepository.findBySucursal(sucursal)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO obtenerInventarioPorId(Integer id) {
        Inventario inventario = findInventarioById(id);
        return toDTO(inventario);
    }

    // --- MÉTODO POST ---
    public InventarioDTO guardarRegistroInventario(InventarioDTO dto) {
        // 1. Validamos contra las tablas locales replicadas
        Producto producto = productoRepository.findById(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado en el contexto de Inventario. ID: " + dto.getProducto().getIdProducto()));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursal().getIdSucursal())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada en el contexto de Inventario. ID: " + dto.getSucursal().getIdSucursal()));

        // 2. Verificamos que no exista ya un registro para este producto en esta sucursal
        inventarioRepository.findByProductoAndSucursal(producto, sucursal).ifPresent(inv -> {
            throw new IllegalStateException("Ya existe un registro de inventario para este producto en esta sucursal.");
        });

        // 3. Creamos la nueva entidad
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        inventario.setStockMinimo(dto.getStockMinimo());
        inventario.setStockMaximo(dto.getStockMaximo());
        inventario.setStockActual(dto.getStockActual());
        inventario.setFechaActualizacion(LocalDate.now());

        return toDTO(inventarioRepository.save(inventario));
    }

    // --- MÉTODOS PUT/PATCH ---
    public InventarioDTO actualizarRegistroInventario(Integer id, InventarioDTO dto) {
        Inventario inventarioExistente = findInventarioById(id);
        
        Producto producto = productoRepository.findById(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado. ID: " + dto.getProducto().getIdProducto()));
        
        Sucursal sucursal = sucursalRepository.findById(dto.getSucursal().getIdSucursal())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada. ID: " + dto.getSucursal().getIdSucursal()));
        
        inventarioExistente.setProducto(producto);
        inventarioExistente.setSucursal(sucursal);
        inventarioExistente.setStockMinimo(dto.getStockMinimo());
        inventarioExistente.setStockMaximo(dto.getStockMaximo());
        inventarioExistente.setStockActual(dto.getStockActual());
        inventarioExistente.setFechaActualizacion(LocalDate.now());

        return toDTO(inventarioRepository.save(inventarioExistente));
    }

    public InventarioDTO actualizarStock(Integer inventarioId, Integer nuevoStock) {
        Inventario inventario = findInventarioById(inventarioId);
        inventario.setStockActual(nuevoStock);
        inventario.setFechaActualizacion(LocalDate.now());
        return toDTO(inventarioRepository.save(inventario));
    }

    // --- MÉTODO DELETE ---
    public void eliminarRegistroInventario(Integer id) {
        if (!inventarioRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Registro de inventario no encontrado con ID: " + id);
        }
        inventarioRepository.deleteById(id);
    }
    
    // --- MÉTODOS DE AYUDA ---
    private Inventario findInventarioById(Integer id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de inventario no encontrado con ID: " + id));
    }

    private InventarioDTO toDTO(Inventario inventario) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdInventario(inventario.getIdInventario());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setStockMaximo(inventario.getStockMaximo());
        dto.setStockActual(inventario.getStockActual());
        dto.setFechaActualizacion(inventario.getFechaActualizacion());
        
        // Creamos los DTOs anidados a partir de las entidades locales simplificadas
        ProductoDTO pDto = new ProductoDTO();
        pDto.setIdProducto(inventario.getProducto().getIdProducto());
        pDto.setNombre(inventario.getProducto().getNombre());
        dto.setProducto(pDto);

        SucursalDTO sDto = new SucursalDTO();
        sDto.setIdSucursal(inventario.getSucursal().getIdSucursal());
        sDto.setNombreSucursal(inventario.getSucursal().getNombreSucursal());
        dto.setSucursal(sDto);
        
        return dto;
    }
}