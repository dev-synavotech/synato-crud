package com.synato.synato_crud.service;

import com.synato.synato_crud.entity.Product;
import com.synato.synato_crud.exception.ResourceNotFoundException;
import com.synato.synato_crud.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", 99.99);
        testProduct.setId(1L);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.createProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getDescription(), result.getDescription());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Arrange
        Product product2 = new Product("Product 2", "Description 2", 149.99);
        product2.setId(2L);
        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        assertEquals(product2.getName(), result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(999L));

        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedDetails = new Product("Updated Product", "Updated Description", 199.99);
        Product updatedProduct = new Product("Updated Product", "Updated Description", 199.99);
        updatedProduct.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(199.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        Product updatedDetails = new Product("Updated Product", "Updated Description", 199.99);
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(999L, updatedDetails));

        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(999L));

        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
