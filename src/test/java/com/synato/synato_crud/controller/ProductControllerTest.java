package com.synato.synato_crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synato.synato_crud.entity.Product;
import com.synato.synato_crud.exception.ResourceNotFoundException;
import com.synato.synato_crud.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", 99.99);
        testProduct.setId(1L);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Arrange
        Product product2 = new Product("Product 2", "Description 2", 149.99);
        product2.setId(2L);
        List<Product> products = Arrays.asList(testProduct, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[1].name", is("Product 2")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product("Updated Product", "Updated Description", 199.99);
        updatedProduct.setId(1L);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(199.99)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        Product updatedProduct = new Product("Updated Product", "Updated Description", 199.99);
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        // Act & Assert
        mockMvc.perform(delete("/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999L);
    }
}
