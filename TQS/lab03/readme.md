# Lab 03 - Spring Boot

## 3.1 O que é o Spring Boot?

O Spring Boot é uma framework que simplifica o desenvolvimento de aplicações Java ao fornecer configuração automática e reduzir a quantidade de código de configuração necessário.

**Aplicações empresariais** são aplicações de negócio de grande escala que lidam com operações complexas como gestão de dados, processamento de transações e servir múltiplos utilizadores simultaneamente. Precisam de ser fiáveis, seguras e escaláveis.

**Principais frameworks Java empresariais** incluem Spring Framework, Jakarta EE (anteriormente Java EE) e Hibernate para persistência de dados.

**Relação entre Spring Boot e Spring Framework**: O Spring Boot é construído sobre o Spring Framework. Enquanto o Spring Framework fornece as funcionalidades centrais como injeção de dependências e programação orientada a aspectos, o Spring Boot adiciona configuração automática e servidores embebidos para tornar o desenvolvimento mais rápido e fácil. O Spring Boot também funciona com as especificações Jakarta EE, usando muitas das mesmas anotações e conceitos.

**Exemplos de configuração automática do Spring Boot**:
- Servidor web embebido (Tomcat, Jetty) - não é necessário fazer deploy para servidor externo
- Configuração de base de dados - configura automaticamente a datasource quando o driver da BD é detetado
- Configuração de segurança - configuração básica de autenticação quando Spring Security é incluído
- Serialização JSON - configuração automática do Jackson para APIs REST

**Relação com aprendizagem anterior**: O Spring Boot conecta-se com cadeiras anteriores através de:
- Utilização de conceitos de programação orientada a objetos das cadeiras de programação
- Aplicação de padrões de design aprendidos em pds
- Construção sobre conhecimentos de desenvolvimento web das cadeiras de programação web
- Utilização de conceitos de testes para testes unitários e de integração
- Implementação de conceitos de bases de dados das cadeiras de BD

## 3.2 Tutorial: REST web services

### Passo 1 - Criar o projeto com Spring Initializr

Criei um novo projeto Spring Boot usando o Spring Initializr com:
- Maven como build tool
- Java 17
- Dependência: Spring Web
- Group: com.example
- Artifact: rest-service

O projeto foi criado com a estrutura básica Maven e inclui o Spring Boot Starter Web.

### Passo 2 - Criar a classe de representação (Greeting)

Criámos a classe `Greeting.java` como um record Java que representa a resposta JSON:
- `id`: identificador único da greeting
- `content`: conteúdo textual da greeting

### Passo 3 - Criar o Controller REST

Criámos `GreetingController.java` com:
- Anotação `@RestController` para marcar como controller REST
- Método `greeting()` com `@GetMapping("/greeting")`
- Parâmetro `name` com valor default "World"
- Contador automático para IDs únicos

### Passo 4 - Executar a aplicação

Executei a aplicação com `./mvnw spring-boot:run` e observei o output do console.

**Observações sobre o Tomcat no console:**

No output da aplicação, vemos várias referências ao Tomcat:
- `Tomcat initialized with port 8080 (http)`
- `Starting service [Tomcat]`
- `Starting Servlet engine: [Apache Tomcat/10.1.46]`

**O que é o Tomcat e porque é usado:**
O Apache Tomcat é um servidor web e contentor de servlets Java. No contexto do Spring Boot:
- É o servidor web **embebido** por defeito
- Permite executar aplicações web sem necessidade de instalar um servidor externo
- Gere pedidos HTTP e serve as respostas JSON da nossa API REST
- Funciona internamente para processar os endpoints definidos nos controllers

Isto demonstra uma das principais vantagens do Spring Boot: configuração automática de infraestrutura (neste caso, um servidor web completo) sem configuração manual.

### Passo 5 - Testar o serviço

Aplicação executada com sucesso! Testes realizados:

1. **Endpoint básico:**
   - `curl http://localhost:8080/greeting`
   - Resposta: `{"id":1,"content":"Hello, World!"}`

2. **Endpoint com parâmetro:**
   - `curl "http://localhost:8080/greeting?name=User"`
   - Resposta: `{"id":2,"content":"Hello, User!"}`

**Observações importantes:**
- O ID incrementa automaticamente (1, 2, 3...) devido ao `AtomicLong counter`
- O parâmetro `name` substitui o valor default "World"
- O Spring Boot converteu automaticamente o objeto `Greeting` para JSON
- O Tomcat embebido gere todos os pedidos HTTP sem configuração manual

---

## 3.3 Backend para Meals Booking

### a) O que vou fazer

No Lab 1 fiz um serviço de reservas de refeições em Java normal. Agora vou transformar isso numa API REST com Spring Boot.

**O que tinha no Lab 1:**
- `MealsBookingService` - fazia as reservas
- `Booking` - guardava os dados de cada reserva
- Métodos: fazer reserva, cancelar, fazer check-in

**O que vou criar agora:**
- `@Entity Booking` - para guardar na base de dados
- `@Repository` - para aceder à base de dados
- `@Service` - com a lógica de negócio do Lab 1
- `@RestController` - para receber pedidos HTTP

### b) Criar novo projeto

**Onde:** Vai ao https://start.spring.io

**O que fazer:**
1. Maven Project
2. Java 21 
3. Group: `tqs.lab3`
4. Artifact: `meals-booking`
5. Dependencies: adicionar **Spring Web**, **Spring Data JPA**, **H2 Database**
6. Generate → download do zip
7. Extrair no diretório lab03

### c) Estrutura que vais criar

**Pasta:** `src/main/java/tqs/lab3/mealsbooking/`

**Ficheiros a criar:**
1. `entity/Booking.java` - os dados da reserva
2. `entity/BookingStatus.java` - enum (ACTIVE, USED, CANCELLED) 
3. `repository/BookingRepository.java` - acesso à BD
4. `service/BookingService.java` - lógica de negócio do Lab 1
5. `controller/BookingController.java` - endpoints REST

### d) Passo a passo - Entity (dados)

**Ficheiro:** `entity/Booking.java`

**O que fazer:**
- Copiar a classe `Booking` do Lab 1
- Adicionar anotações JPA:
  - `@Entity` na classe
  - `@Id` no token
  - `@Enumerated` no status
- Adicionar construtor vazio (JPA precisa)

**Ficheiro:** `entity/BookingStatus.java`
- Copiar o enum do Lab 1 (mesmo código)

### e) Passo a passo - Repository (base de dados)

**Ficheiro:** `repository/BookingRepository.java`

**O que fazer:**
- Criar interface que extends `JpaRepository<Booking, String>`
- Adicionar `@Repository`
- Spring cria automaticamente os métodos save(), findById(), etc.

### f) Passo a passo - Service (lógica de negócio)

**Ficheiro:** `service/BookingService.java`

**O que fazer:**
- Copiar a lógica do `MealsBookingService` do Lab 1
- Trocar o `HashMap<String, Booking>` por `BookingRepository`
- Adicionar `@Service` na classe
- Adicionar `@Autowired BookingRepository`
- Métodos principais:
  - `bookMeal()` → usa `repository.save()`
  - `getBookingDetails()` → usa `repository.findById()`
  - `cancelBooking()` → altera status e `repository.save()`
  - `checkIn()` → altera status e `repository.save()`

### g) Passo a passo - Controller (API REST)

**Ficheiro:** `controller/BookingController.java`

**O que fazer:**
- Adicionar `@RestController` na classe
- Adicionar `@Autowired BookingService`
- Criar endpoints:
  - `POST /bookings` → chama `bookingService.bookMeal()`
  - `GET /bookings/{token}` → chama `bookingService.getBookingDetails()`
  - `PUT /bookings/{token}/cancel` → chama `bookingService.cancelBooking()`
  - `PUT /bookings/{token}/checkin` → chama `bookingService.checkIn()`

### h) Testar com Postman

**O que fazer:**
1. Executar aplicação: `./mvnw spring-boot:run`
2. Testar endpoints no Postman:
   - POST http://localhost:8080/bookings (criar reserva)
   - GET http://localhost:8080/bookings/{token} (ver reserva)
   - PUT http://localhost:8080/bookings/{token}/cancel (cancelar)
   - PUT http://localhost:8080/bookings/{token}/checkin (check-in)

### i) Depois: PostgreSQL + Docker

**O que fazer:**
1. Instalar Docker
2. Executar: `docker run --name postgresdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=meals_db -p 5432:5432 -d postgres:latest`
3. No `pom.xml`: remover H2, adicionar PostgreSQL
4. No `application.properties`: configurar ligação à BD

**Resultado final:** API REST funcional que substitui o HashMap do Lab 1 por uma base de dados real!

