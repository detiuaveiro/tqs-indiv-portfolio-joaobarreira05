# Lab 5 - Web Layer Test Automation

## Estrutura do Projeto

```
lab05/
├── ex1-selenium/          # Testes Selenium (5.1-5.4)
│   ├── pom.xml
│   └── src/test/java/tqs/lab5/
│       ├── HelloWorldSeleniumTest.java       # 5.1a - Basic WebDriver
│       ├── HelloWorldSeleniumJupiterTest.java # 5.1c - Selenium-Jupiter
│       ├── BlazeDemoTest.java                # 5.2 - BlazeDemo booking
│       ├── BookSearchTest.java               # 5.3 - Locators e waits
│       └── pageobject/                       # 5.4 - Page Object Pattern
│           ├── HomePage.java
│           ├── FlightsPage.java
│           ├── PurchasePage.java
│           ├── ConfirmationPage.java
│           └── BlazeDemoPageObjectTest.java
└── ex2-playwright/        # Testes Playwright (5.5-5.8)
    ├── pom.xml
    └── src/test/java/tqs/lab5/
        ├── HelloWorldPlaywrightTest.java         # 5.5a - Basic
        ├── HelloWorldPlaywrightExtensionTest.java # 5.5c - @UsePlaywright
        ├── BlazeDemoPlaywrightTest.java          # 5.6 - Codegen style
        ├── BookSearchPlaywrightTest.java         # 5.7 - Locators
        └── pageobject/                           # 5.8 - Page Objects
            ├── BookSearchPage.java
            └── BookSearchPageObjectTest.java
```

## Como Executar

### Selenium Tests
```bash
cd ex1-selenium
mvn test
```

### Playwright Tests
```bash
cd ex2-playwright
# Instalar browsers Playwright (primeira vez)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
# Executar testes
mvn test
```

### Playwright Codegen (5.6)
```bash
cd ex2-playwright
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen blazedemo.com"
```

---

## 5.3b/5.6c - Análise de Locators

### Robustez dos Locators (mais para menos robusto):

| Estratégia | Robustez | Exemplo |
|------------|----------|---------|
| `data-testid` | ⭐⭐⭐⭐⭐ | `[data-testid='search-btn']` |
| `id` | ⭐⭐⭐⭐ | `#inputName` |
| Role-based | ⭐⭐⭐⭐ | `getByRole(BUTTON, "Submit")` |
| `name` | ⭐⭐⭐ | `input[name='email']` |
| CSS class | ⭐⭐ | `.submit-button` |
| XPath posicional | ⭐ | `//div[3]/form/input[1]` |

### Playwright vs Selenium Locators

**Playwright prefere:**
- `getByRole()` - semântico, robusto
- `getByLabel()` - para campos de formulário
- `getByTestId()` - explicit test hooks
- `getByText()` - para botões/links

**Selenium usa:**
- `By.id()`, `By.name()`, `By.cssSelector()`
- `By.linkText()`, `By.xpath()`

---

## 5.4 - Page Object Pattern

### Benefícios:
1. **Separação de concerns** - lógica de página vs lógica de teste
2. **Reutilização** - métodos da página em múltiplos testes
3. **Manutenção** - mudanças na UI só afetam a Page Object
4. **Legibilidade** - testes mais claros e fluentes

### Exemplo:
```java
// Sem Page Object (verbose)
driver.findElement(By.name("fromPort")).sendKeys("Boston");
driver.findElement(By.name("toPort")).sendKeys("Berlin");
driver.findElement(By.cssSelector("input[type='submit']")).click();

// Com Page Object (limpo)
homePage.searchFlights("Boston", "Berlin");
```

---

## Selenium vs Playwright

| Aspecto | Selenium | Playwright |
|---------|----------|------------|
| Auto-wait | Manual (WebDriverWait) | Automático |
| Setup | WebDriverManager | CLI install |
| Browsers | Instalados no sistema | Próprios binaries |
| API | Imperativa | Fluente |
| Flaky tests | Mais comum | Menos comum |
