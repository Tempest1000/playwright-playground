package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ParallelMultiBrowserTests {
    @ParameterizedTest(name = "WebTables runs on {0}")
    @ValueSource(strings = {"chromium", "firefox", "webkit"})
    void webTables_loadsQuickly_andAddsRow(String browserName) {
        System.out.printf("[%s] Running %s%n", Thread.currentThread().getName(), browserName);

        // Create a separate Playwright for each test
        try (Playwright playwright = Playwright.create()) {
            BrowserType browserType = switch (browserName) {
                case "firefox" -> playwright.firefox();
                case "webkit" -> playwright.webkit();
                default -> playwright.chromium();
            };

            try (Browser browser = browserType.launch(
                    new BrowserType.LaunchOptions().setHeadless(true))) {

                Page page = browser.newPage();

                long start = System.nanoTime();
                page.navigate("https://demoqa.com/webtables",
                        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                page.waitForSelector(".rt-table");
                long durationMs = (System.nanoTime() - start) / 1_000_000;
                System.out.println(browserName + " load time: " + durationMs + " ms");

                String email = "test.user@" + browserName + ".example.com";
                page.locator("#addNewRecordButton").click();
                page.locator("#firstName").fill("Test");
                page.locator("#lastName").fill("User");
                page.locator("#userEmail").fill(email);
                page.locator("#age").fill("30");
                page.locator("#salary").fill("9000");
                page.locator("#department").fill("QA");
                page.locator("#submit").click();

                page.waitForSelector("#registration-form-modal",
                        new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));

                assertThat(page.locator(".rt-tbody")).containsText(email);
            }
        }
    }
}
