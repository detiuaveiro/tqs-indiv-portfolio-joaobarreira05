# Lab 04 - Testing in Spring Boot

## 4.1 Tipos de Testes no Spring Boot

### A) Repository Test - Testar a camada de dados
**Ficheiro:** `A_EmployeeRepositoryTest.java`

**O que faz:**
- Testa apenas o Repository (acesso à base de dados)
- Usa `@DataJpaTest` - carrega apenas o contexto de dados
- Usa `TestEntityManager` para inserir dados diretamente na BD
- Base de dados H2 em memória (rápido)

**Exemplo essencial:**
```java
@DataJpaTest
class A_EmployeeRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private EmployeeRepository repository;
    
    @Test
    void testFindById() {
        Employee emp = new Employee("João", "joao@ua.pt");
        entityManager.persistAndFlush(emp); // inserir na BD
        
        Employee found = repository.findById(emp.getId()).orElse(null);
        assertThat(found.getName()).isEqualTo("João");
    }
}
```

### B) Service Unit Test - Testar lógica de negócio
**Ficheiro:** `B_EmployeeService_UnitTest.java`

**O que faz:**
- Testa apenas o Service (lógica de negócio)
- Usa `@Mock` para simular o Repository
- Não usa base de dados (muito rápido)
- Verifica se o Service chama corretamente o Repository

**Exemplo essencial:**
```java
@ExtendWith(MockitoExtension.class)
class B_EmployeeService_UnitTest {
    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    
    @Test
    void testGetAllEmployees() {
        Employee emp1 = new Employee("João", "joao@ua.pt");
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(emp1));
        
        List<Employee> employees = employeeService.getAllEmployees();
        assertThat(employees).hasSize(1);
    }
}
```

### C) Controller Test (com Service mockado)
**Ficheiro:** `C_EmployeeController_WithMockServiceTest.java`

**O que faz:**
- Testa apenas o Controller (REST endpoints)
- Usa `@WebMvcTest` - carrega apenas o contexto web
- Usa `@MockBean` para simular o Service
- Testa HTTP requests/responses sem servidor completo

**Exemplo essencial:**
```java
@WebMvcTest(EmployeeRestController.class)
class C_EmployeeController_WithMockServiceTest {
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private EmployeeService service;
    
    @Test
    void testGetAllEmployees() throws Exception {
        Employee emp = new Employee("João", "joao@ua.pt");
        when(service.getAllEmployees()).thenReturn(Arrays.asList(emp));
        
        mvc.perform(get("/api/employees"))
           .andExpect(status().isOk())
           .andExpected(jsonPath("$", hasSize(1)));
    }
}
```

### D) Integration Test (com MockMvc)
**Ficheiro:** `D_EmployeeRestControllerIT.java`

**O que faz:**
- Testa toda a aplicação (Controller + Service + Repository + BD)
- Usa `@SpringBootTest` - carrega contexto completo
- Usa `MockMvc` para simular pedidos HTTP
- Base de dados real (H2 em memória)

**Exemplo essencial:**
```java
@SpringBootTest
@AutoConfigureTestDatabase
class D_EmployeeRestControllerIT {
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private EmployeeRepository repository;
    
    @Test
    void testCreateEmployee() throws Exception {
        mvc.perform(post("/api/employees")
           .contentType(MediaType.APPLICATION_JSON)
           .content("{\"name\":\"João\",\"email\":\"joao@ua.pt\"}"))
           .andExpect(status().isCreated());
           
        assertThat(repository.findAll()).hasSize(1);
    }
}
```

### E) Integration Test (com RestTemplate)
**Ficheiro:** `E_EmployeeRestControllerTemplateIT.java`

**O que faz:**
- Testa toda a aplicação como um cliente externo
- Usa `@SpringBootTest` com servidor web real
- Usa `TestRestTemplate` - cliente HTTP real
- Testa marshalling/unmarshalling JSON

**Exemplo essencial:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class E_EmployeeRestControllerTemplateIT {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetAllEmployees() {
        Employee emp = new Employee("João", "joao@ua.pt");
        // POST para criar
        ResponseEntity<Employee> response = restTemplate.postForEntity("/api/employees", emp, Employee.class);
        
        // GET para verificar
        Employee[] employees = restTemplate.getForObject("/api/employees", Employee[].class);
        assertThat(employees).hasSize(1);
    }
}
```

## Diferenças Importantes

### @Mock vs @MockBean
- **@Mock** (Mockito): para unit tests simples, sem contexto Spring
- **@MockBean** (Spring Boot): substitui beans no contexto Spring, para integration tests

### application-integrationtest.properties
- Ficheiro de configuração específico para testes de integração
- Usado quando o perfil "integrationtest" está ativo
- Permite configurar BD diferente, logs, etc. só para testes

**Exemplo:**
```properties
# application-integrationtest.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.org.springframework.web=DEBUG
```

## Resumo dos Tipos de Teste

| Tipo | Anotação | O que testa | Velocidade |
|------|----------|-------------|------------|
| A) Repository | `@DataJpaTest` | Só Repository + BD | Rápido |
| B) Service Unit | `@ExtendWith(MockitoExtension)` | Só Service (com mocks) | Muito rápido |
| C) Controller | `@WebMvcTest` | Só Controller (com mock service) | Rápido |
| D) Integration (MockMvc) | `@SpringBootTest` | Toda a aplicação | Médio |
| E) Integration (RestTemplate) | `@SpringBootTest` + servidor | Como cliente externo | Mais lento |

---

## 4.3 Respostas às Perguntas do Lab

### @Mock vs @MockBean - Qual a diferença?

| Característica | @Mock | @MockBean |
|----------------|-------|-----------|
| **Biblioteca** | Mockito puro | Spring Boot Test |
| **Contexto Spring** | Não requer | Requer contexto Spring |
| **Uso típico** | Unit tests sem Spring | Integration tests com Spring |
| **Como funciona** | Cria um mock simples | Substitui bean no Application Context |
| **Velocidade** | Muito rápido | Mais lento (carrega contexto) |

**Exemplo @Mock** (unit test):
```java
@ExtendWith(MockitoExtension.class)
class ServiceUnitTest {
    @Mock
    private Repository repository;  // Mock simples
    
    @InjectMocks
    private ServiceImpl service;    // Injecta o mock
}
```

**Exemplo @MockBean** (integration test):
```java
@WebMvcTest(Controller.class)
class ControllerTest {
    @MockBean
    private Service service;  // Substitui no contexto Spring
}
```

### Papel do application-integrationtest.properties

Este ficheiro permite configurar um ambiente diferente especificamente para testes de integração:

1. **Ativação**: Via `@TestPropertySource` ou `@ActiveProfiles("integrationtest")`
2. **Uso típico**: Ligar a uma base de dados PostgreSQL real em vez do H2 em memória
3. **Configurações comuns**:
   - URL da base de dados real
   - Credenciais de acesso
   - Nível de logging mais detalhado
   - DDL strategy (create-drop para limpar entre testes)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meals_db
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.org.springframework.web=DEBUG
```

### Top-Down Approach e TDD

**Pergunta**: Can a top-down approach help with TDD practice?

**Sim, e de forma significativa!** A abordagem top-down complementa o TDD:

1. **Começa pelos requisitos de utilizador** → Define os endpoints REST primeiro
2. **Testes de Controller** → Define o contrato da API antes da implementação
3. **Mock dos Services** → Permite testar Controller sem Service implementado
4. **Depois implementa Services** → Com testes já definidos
5. **Finalmente Repository** → Integração com base de dados

**Vantagens:**
- Foco na interface/contrato antes da implementação
- Permite trabalho paralelo (frontend pode começar com mocks)
- Identifica problemas de design cedo
- Garante que a API é "testável" desde o início

### Cenário de Aluguer de Automóveis

**Requisito**: "Find a car that provides a suitable replacement for some given car"

**Testes Recomendados**:

| Tipo | Objetivo | Como Implementar |
|------|----------|------------------|
| **Unit Test** | Testar lógica de "similaridade" | `@ExtendWith(MockitoExtension)` no CarMatchingService |
| **Repository Test** | Query para carros disponíveis | `@DataJpaTest` com findBySegmentAndMotorTypeAndAvailable |
| **Controller Test** | Endpoint GET /api/cars/{id}/replacement | `@WebMvcTest` com MockBean |
| **Integration Test** | Fluxo completo | `@SpringBootTest` com TestRestTemplate |

```java
// Service Unit Test - testar lógica de matching
@Test
void whenFindReplacement_thenReturnMatchingCar() {
    Car original = new Car("BMW 320", "SEDAN", "DIESEL");
    Car replacement = new Car("Audi A4", "SEDAN", "DIESEL");
    
    when(carRepository.findAvailableByCriteria("SEDAN", "DIESEL"))
        .thenReturn(List.of(replacement));
    
    Car found = carService.findReplacement(original);
    assertThat(found.getSegment()).isEqualTo("SEDAN");
}
```

---

## 4.4 REST Assured

REST Assured é uma biblioteca Java para testar APIs REST com sintaxe BDD:

```java
given()
    .contentType(ContentType.JSON)
    .body("{\"studentId\":\"stu1\",\"serviceShift\":\"LUNCH\"}")
.when()
    .post("/api/bookings")
.then()
    .statusCode(200)
    .body("token", hasLength(8));
```

**Vantagens sobre TestRestTemplate:**
- Sintaxe mais fluente e legível
- Assertions integradas
- Suporte nativo para JSON Path
- Melhor para documentação de testes

