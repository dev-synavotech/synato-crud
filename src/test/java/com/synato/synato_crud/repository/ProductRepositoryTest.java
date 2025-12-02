package com.synato.synato_crud.repository;

import com.synato.synato_crud.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", 99.99);
    }

    @Test
    void save_ShouldPersistProduct() {
        // Act
        Product savedProduct = productRepository.save(testProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getDescription(), savedProduct.getDescription());
        assertEquals(testProduct.getPrice(), savedProduct.getPrice());
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
    }

    @Test
    void findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        Product product1 = new Product("Product 1", "Description 1", 99.99);
        Product product2 = new Product("Product 2", "Description 2", 149.99);
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertEquals(2, products.size());
    }

    @Test
    void delete_ShouldRemoveProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        Long productId = savedProduct.getId();

        // Act
        productRepository.delete(savedProduct);
        entityManager.flush();

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void update_ShouldModifyProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);

        // Act
        savedProduct.setName("Updated Name");
        savedProduct.setDescription("Updated Description");
        savedProduct.setPrice(199.99);
        Product updatedProduct = productRepository.save(savedProduct);
        entityManager.flush();

        // Assert
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(199.99, updatedProduct.getPrice());
    }
}
