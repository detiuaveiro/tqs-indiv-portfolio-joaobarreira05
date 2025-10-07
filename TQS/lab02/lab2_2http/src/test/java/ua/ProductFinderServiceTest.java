package ua;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductFinderServiceTest {

    @Test
    void testFindProductDetailsReturnsProduct() {
        // Arrange
        ISimpleHttpClient httpClient = mock(ISimpleHttpClient.class);
        // JSON simplificado, só com os campos necessários para o teste
        String productJson = """
        {
            "id": 3,
            "title": "Mens Cotton Jacket",
            "price": 55.99,
            "description": "great outerwear jacket",
            "category": "men's clothing",
            "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg"
        }
        """;
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/3")).thenReturn(productJson);

        ProductFinderService service = new ProductFinderService(httpClient);

        // Act
        Optional<Product> result = service.findProductDetails(3);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getId());
        assertEquals("Mens Cotton Jacket", result.get().getTitle());
    }

    @Test
    void testFindProductDetailsReturnsEmptyOptional() {
        // Arrange
        ISimpleHttpClient httpClient = mock(ISimpleHttpClient.class);
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/300")).thenReturn(""); // resposta vazia

        ProductFinderService service = new ProductFinderService(httpClient);

        // Act
        Optional<Product> result = service.findProductDetails(300);

        // Assert
        assertTrue(result.isEmpty());
    }
}