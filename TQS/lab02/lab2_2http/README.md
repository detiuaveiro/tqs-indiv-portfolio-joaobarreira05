# Lab 2.2 - HTTP Client com Mocking

## How would mocking be used in this scenario?

O mocking é usado para **isolar o código sob teste** das dependências externas. Neste cenário:

1. **Problema**: O `ProductFinderService` depende de chamadas HTTP para uma API externa
2. **Solução com Mocking**: 
   - Criamos um mock do `ISimpleHttpClient`
   - Simulamos as respostas HTTP sem fazer pedidos reais
   - Controlamos exactamente que dados o método `doHttpGet()` retorna

**Exemplo prático:**
```java
// Mock do cliente HTTP
ISimpleHttpClient httpClient = mock(ISimpleHttpClient.class);

// Simulação da resposta da API
when(httpClient.doHttpGet("https://fakestoreapi.com/products/3"))
    .thenReturn(productJson);
```

**Vantagens:**
- Testes **rápidos** (sem chamadas de rede)
- Testes **determinísticos** (sempre a mesma resposta)
- Testes **isolados** (não dependem da API estar online)
- Controlo total sobre **cenários de erro** (respostas vazias, etc.)

## Implementação dos Testes

### Caso de Sucesso: `findProductDetails(3)` retorna produto válido
- Mock retorna JSON com dados do produto
- Verifica parsing correcto
- Confirma que `Optional` não está vazio

### Caso de Falha: `findProductDetails(300)` retorna Optional vazio  
- Mock retorna string vazia
- Verifica que o serviço trata correctamente respostas inválidas
