# Playwright

## Documentation

https://playwright.dev/java/docs/intro

## Run from Command Line

```declarative
PS C:\code\java\playwright-playground> mvn compile exec:java '-Dexec.mainClass=org.example.Main'
```

## Playwright Terminology

Selector - text like `String selector = "button.submit";`
Locator - smart handle like `Locator button = page.locator("button.submit");`

| Concept      | Description                                                                                                                                                                   | Analogy (Selenium)                                                    |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- |
| **Selector** | A *string expression* (like `"text=Login"` or `"#submit"`) that tells Playwright **how** to find an element.                                                                  | `By.cssSelector("#submit")` or `By.xpath("//button[text()='Login']")` |
| **Locator**  | A **Playwright object** created *from* a selector — it **encapsulates a query**, supports lazy evaluation, and gives you rich APIs (`click()`, `fill()`, `isVisible()`, etc). | Roughly like `WebElement`, but *deferred* and smarter.                |

## Playwright Locator Quick Reference

https://playwright.dev/java/docs/locators
https://playwright.dev/java/docs/other-locators
https://playwright.dev/java/docs/other-locators#css-locator

| **Strategy** | **Example** | **Notes** |
|---------------|-------------|-----------|
| **CSS (Class / ID / Attribute)** | `page.locator("a.getStarted_Sjon")`, `page.locator("#loginButton")`, `page.locator("[href='/home']")` | ✅ Fast, standard, flexible |
| **Text** | `page.locator("text=Get started")` | ✅ Readable, semantic |
| **XPath** | `page.locator("xpath=//a[text()='Get started']")` | ⚠️ Verbose, slower |
| **Role (ARIA)** | `page.getByRole(AriaRole.LINK, opts.setName("Get started"))` | ✅ Accessible, resilient |
| **Test ID** | `page.getByTestId("get-started-link")` | ✅ Best for testable apps |
| **Chained** | `page.locator("div.buttons_pzbO").locator("a.getStarted_Sjon")` | ✅ Scoped precision |


## Playwright versus Selenium

| **Playwright Locator** | **Selenium Equivalent** | **Type of Selector** |
|--------------------------|--------------------------|------------------------|
| `page.locator("#id")` | `By.id("id")` | **ID selector** |
| `page.locator(".className")` | `By.className("className")` or `By.cssSelector(".className")` | **Class selector** |
| `page.locator("a.getStarted_Sjon")` | `By.cssSelector("a.getStarted_Sjon")` | **CSS selector** |
| `page.locator("text=Get started")` | `By.xpath("//*[text()='Get started']")` | **Text selector** *(Playwright-only shorthand)* |
| `page.locator("xpath=//a[@class='getStarted_Sjon']")` | `By.xpath("//a[@class='getStarted_Sjon']")` | **XPath selector** |
| `page.locator("[href='/java/docs/intro']")` | `By.cssSelector("[href='/java/docs/intro']")` | **Attribute selector** |
| `page.getByRole("link", new Page.GetByRoleOptions().setName("Get started"))` | *(no direct equivalent)* | **ARIA role selector** *(accessibility-aware)* |

## Sample Selectors

For an HTML element like

```html
<a class="getStarted_Sjon" href="/java/docs/intro">Get started</a>
```

Playwright’s default selector engine uses CSS syntax, just like Selenium’s By.cssSelector

Examples:
```java
page.locator("a.getStarted_Sjon");          // tag + class
page.locator(".getStarted_Sjon");           // class only
page.locator("a[href='/java/docs/intro']"); // by attribute
page.locator("a.getStarted_Sjon[href='/java/docs/intro']");
```
Equivalent Selenium code:
```java
driver.findElement(By.cssSelector("a.getStarted_Sjon"));
```

Playwright also offers text selectors like this:
```java
page.locator("text=Get started");          // exact text
page.locator("text='Get started'");        // equivalent
page.locator("a:has-text('Get started')"); // CSS + text hybrid
```

## ARIA selectors
These are very powerful and let you find elements in the same way that screenreaders and accessibility API's do, not just by looking at raw HTML.
Playwright's `getByRole()` method uses the Accessible Name and Role Model, i.e. instead of looking for `<a>` or `.className` Playwright looks for something like `button`, `link`

Using our existing example:

```html
<a class="getStarted_Sjon" href="/java/docs/intro">Get started</a>
```

The ARIA selector might look like this:

```java
page.getByRole(
        AriaRole.LINK, 
        new Page.GetByRoleOptions().setName("Get started")
);
```

* `AriaRole.LINK` → This enum constant represents the accessibility role (like "link", "button", "textbox", etc.).
* `new Page.GetByRoleOptions()` → This creates a new options object (a small helper class provided by Playwright).
* `.setName("Get started")` → Sets the accessible name filter (the visible text or aria-label of the element).

If the class name or the structure of the HTML changes, it may not break this selector.

Reviewed in the browser Dev Tools > Elements > side nav Accessibility
The important parts are the `name` and the `Role:link`

```properties
Name: "GET STARTED"
aria-labelledby: Not specified
aria-label: Not specified
Contents: "GET STARTED"
title: Not specified
Role: link
Focusable: true
Focused: true
url: "https://playwright.dev/java/docs/intro"
```

## Running JUnit Tests

### From Windows Terminal command line

```properties
$env:PWDEBUG = "1";mvn test -Dtest=PlaywrightSmokeTests
```

### Config in IntelliJ

Open Run > Edit Configurations…

In the top menu bar:
Run > Edit Configurations…

Select your test configuration, for example:
```
PWDEBUG=1;PLAYWRIGHT_JAVA_SRC=C:\code\java\playwright-playground\src\main\java;C:\code\java\playwright-playground\src\test\java
```

## Running Tests in Parallel

```properties
mvn test -Dtest=ParallelMultiBrowserTests -DforkCount=2
```