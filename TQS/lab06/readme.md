# Lab 6 - BDD with Cucumber Framework

## Estrutura do Projeto

```
lab06/
├── ex1-calculator/          # 6.1 - RPN Calculator
│   ├── src/main/java/tqs/lab6/Calculator.java
│   └── src/test/
│       ├── java/tqs/lab6/
│       │   ├── CalculatorSteps.java
│       │   └── CucumberTest.java
│       └── resources/tqs/lab6/calculator.feature
├── ex2-library/             # 6.2 - Library Book Search
│   ├── src/main/java/tqs/lab6/
│   │   ├── Book.java
│   │   └── Library.java
│   └── src/test/
│       ├── java/tqs/lab6/
│       │   ├── LibrarySteps.java    # @ParameterType, @DataTableType
│       │   └── CucumberTest.java
│       └── resources/tqs/lab6/library.feature
└── ex3-web/                 # 6.3 - Web Automation
    └── src/test/
        ├── java/tqs/lab6/
        │   ├── BookSearchSteps.java  # Selenium WebDriver
        │   └── CucumberTest.java
        └── resources/tqs/lab6/booksearch.feature
```

## Como Executar

```bash
# 6.1 - Calculator
cd ex1-calculator && mvn test

# 6.2 - Library
cd ex2-library && mvn test

# 6.3 - Web (requires Chrome)
cd ex3-web && mvn test
```

---

## Conceitos-Chave

### Cucumber Expressions vs Regex

```java
// ❌ Regex (old style)
@When("^I add (\\d+) and (\\d+)$")

// ✅ Cucumber Expressions (best practice)
@When("I add {int} and {int}")
```

### Data Tables (6.2c)

```gherkin
Given the following books:
  | title          | author       | published  |
  | One good book  | Anonymous    | 2013-03-12 |
```

```java
@DataTableType
public Book bookEntry(Map<String, String> entry) {
    return new Book(entry.get("title"), ...);
}
```

### Custom ParameterType (6.2b)

```java
@ParameterType("\\d{4}-\\d{2}-\\d{2}")
public LocalDate iso8601Date(String dateString) {
    return LocalDate.parse(dateString);
}

@When("books published between {iso8601Date} and {iso8601Date}")
public void search(LocalDate from, LocalDate to) { ... }
```

---

## Gherkin Keywords

| Keyword | Uso |
|---------|-----|
| `Feature` | Descrição da funcionalidade |
| `Scenario` | Cenário de teste individual |
| `Given` | Pré-condição |
| `When` | Ação |
| `Then` | Resultado esperado |
| `And/But` | Continuação |
| `Background` | Setup comum a todos cenários |
| `Scenario Outline` | Template com Examples |
