package ua;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;

public class ProductFinderService {
    private static final String API_PRODUCTS = "https://fakestoreapi.com/products";
    private ISimpleHttpClient httpClient;
    
    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public Optional<Product> findProductDetails(Integer productId) {
        String url = API_PRODUCTS + "/" + productId;
        String jsonResponse = httpClient.doHttpGet(url);
        
        // Se a resposta está vazia ou nula, retorna Optional vazio
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            Gson gson = new Gson();
            Product product = gson.fromJson(jsonResponse, Product.class);
            return Optional.ofNullable(product);
        } catch (JsonSyntaxException e) {
            // Se houver erro no parsing, retorna Optional vazio
            return Optional.empty();
        }
    }
}
