Nota curta sobre o Sonar (Lab 01 - Meals)

O Sonar indicou um "security hotspot" na função de geração de tokens porque foi usado `Math.random()` para criar parte do token. `Math.random()` não é adequado para fins de segurança/identificadores únicos fortes.

Sugestão simples: usar `UUID.randomUUID()` ou `SecureRandom` para gerar tokens mais seguros. Exemplo breve em Java:

- `String token = UUID.randomUUID().toString().substring(0,8);`

Isto resolve a observação do Sonar de forma prática e segura.
