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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto producto;
    private Sucursal sucursal;
    private Inventario inventario;
    private InventarioDTO inventarioDTO;

    @BeforeEach
    void setUp() {
        // Configuración de objetos de prueba
        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Perfume Alpha");

        sucursal = new Sucursal();
        sucursal.setIdSucursal(1);
        sucursal.setNombreSucursal("Sucursal Central");

        inventario = new Inventario();
        inventario.setIdInventario(1);
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        inventario.setStockActual(100);
        inventario.setStockMinimo(20);
        inventario.setStockMaximo(200);
        inventario.setFechaActualizacion(LocalDate.now());

        // DTO de prueba
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setIdProducto(1);

        SucursalDTO sucursalDTO = new SucursalDTO();
        sucursalDTO.setIdSucursal(1);

        inventarioDTO = new InventarioDTO();
        inventarioDTO.setProducto(productoDTO);
        inventarioDTO.setSucursal(sucursalDTO);
        inventarioDTO.setStockActual(100);
        inventarioDTO.setStockMinimo(20);
        inventarioDTO.setStockMaximo(200);
    }

    @Test
    @DisplayName("Debería guardar un registro de inventario exitosamente")
    void testGuardarRegistroInventario() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(inventarioRepository.findByProductoAndSucursal(producto, sucursal)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // Act
        InventarioDTO resultado = inventarioService.guardarRegistroInventario(inventarioDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getProducto().getIdProducto()).isEqualTo(1);
        assertThat(resultado.getSucursal().getIdSucursal()).isEqualTo(1);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar si el registro ya existe")
    void testGuardarRegistroInventarioYaExistente() {
        // Arrange
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(inventarioRepository.findByProductoAndSucursal(producto, sucursal)).thenReturn(Optional.of(inventario));

        // Act & Assert
        assertThatThrownBy(() -> inventarioService.guardarRegistroInventario(inventarioDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ya existe un registro de inventario");
    }

    @Test
    @DisplayName("Debería listar el inventario de una sucursal")
    void testListarInventarioPorSucursal() {
        // Arrange
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(inventarioRepository.findBySucursal(sucursal)).thenReturn(List.of(inventario));

        // Act
        List<InventarioDTO> resultados = inventarioService.listarInventarioPorSucursal(1);

        // Assert
        assertThat(resultados).isNotNull().hasSize(1);
        assertThat(resultados.get(0).getIdInventario()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debería actualizar el stock de un registro de inventario")
    void testActualizarStock() {
        // Arrange
        when(inventarioRepository.findById(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        int nuevoStock = 150;

        // Act
        InventarioDTO resultado = inventarioService.actualizarStock(1, nuevoStock);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStockActual()).isEqualTo(nuevoStock);
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar stock si el inventario no existe")
    void testActualizarStockNoEncontrado() {
        // Arrange
        when(inventarioRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventarioService.actualizarStock(99, 150))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Registro de inventario no encontrado con ID: 99");
    }

    @Test
    @DisplayName("Debería eliminar un registro de inventario")
    void testEliminarRegistroInventario() {
        // Arrange
        when(inventarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(inventarioRepository).deleteById(1);

        // Act
        inventarioService.eliminarRegistroInventario(1);

        // Assert
        verify(inventarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un registro que no existe")
    void testEliminarRegistroInventarioNoEncontrado() {
        // Arrange
        when(inventarioRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> inventarioService.eliminarRegistroInventario(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se puede eliminar");
    }
}