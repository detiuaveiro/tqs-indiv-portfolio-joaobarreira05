package ua;

import java.util.Optional;

public class ProductFinderService {
    private static final String API_PRODUCTS = "https://fakestoreapi.com/products";
    private ISimpleHttpClient httpClient;
    
    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public Optional<Product> findProductDetails(Integer productId) {
        // Skeleton implementation - will be implemented later with TDD
        // For now, just compile but don't work
        String url = API_PRODUCTS + "/" + productId;
        httpClient.doHttpGet(url);
        
        // TODO: Parse JSON response and return Product
        // This is just a placeholder to make it compile
        return Optional.empty();
    }
}
