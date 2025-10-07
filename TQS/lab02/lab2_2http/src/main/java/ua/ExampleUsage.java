package ua;

import java.util.Optional;

/**
 * Exemplo simples de utilização do ProductFinderService
 * com cliente HTTP real para testar integração
 */
public class ExampleUsage {
    public static void main(String[] args) {
        // Criar cliente HTTP real
        ISimpleHttpClient httpClient = new SimpleHttpClient();
        
        // Criar serviço
        ProductFinderService service = new ProductFinderService(httpClient);
        
        // Testar com produto que existe (ID 1-20 na FakeStore API)
        System.out.println("=== Teste com produto existente (ID: 1) ===");
        Optional<Product> product1 = service.findProductDetails(1);
        if (product1.isPresent()) {
            Product p = product1.get();
            System.out.println("Produto encontrado:");
            System.out.println("ID: " + p.getId());
            System.out.println("Título: " + p.getTitle());
            System.out.println("Preço: " + p.getPrice());
        } else {
            System.out.println("Produto não encontrado");
        }
        
        System.out.println("\n=== Teste com produto inexistente (ID: 999) ===");
        Optional<Product> product999 = service.findProductDetails(999);
        if (product999.isPresent()) {
            System.out.println("Produto encontrado (inesperado!)");
        } else {
            System.out.println("Produto não encontrado (esperado)");
        }
    }
}
