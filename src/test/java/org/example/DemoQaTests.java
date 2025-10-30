package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoQaTests {

    private static Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
    }

    @AfterAll
    static void afterAll() {
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(40)
        );
        page = browser.newPage();
        page.setDefaultTimeout(10_000);
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
    }

    @Test
    void webTables_loadsQuickly_andAddsRow() {
        // Faster/more stable load measurement
        long start = System.nanoTime();
        page.navigate("https://demoqa.com/webtables",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        page.waitForSelector(".rt-table");

        // Open the Add dialog and ensure it’s visible
        page.locator("#addNewRecordButton").click();
        Locator modal = page.locator("#registration-form-modal");
        // Some builds wrap the content; this keeps it flexible:
        assertThat(modal).isVisible();

        String email = "test.user@example.com";

        // Fill all fields (IDs are stable)
        page.locator("#firstName").fill("Test");
        page.locator("#lastName").fill("User");
        page.locator("#userEmail").fill(email);
        page.locator("#age").fill("30");
        page.locator("#salary").fill("9000");
        page.locator("#department").fill("QA");

        // Find Submit by role (more resilient than raw #id), scroll, ensure enabled, click
        Locator submit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        submit.scrollIntoViewIfNeeded();
        assertThat(submit).isEnabled();
        submit.click();

        // Wait for the modal to close (hidden or detached)
        modal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        // Now verify the new row is in the table
        Locator tableBody = page.locator(".rt-tbody");
        assertThat(tableBody).containsText(email);
    }

    @Test
    void textBox_form_submits_andRendersOutput() {
        page.navigate("https://demoqa.com/text-box");

        String name = "Example Tester";
        String email = "example.tester@example.com";
        String currentAddr = "123 Test Lane\nDallas, TX";
        String permanentAddr = "456 Sample Ave\nAustin, TX";

        page.locator("#userName").fill(name);
        page.locator("#userEmail").fill(email);
        page.locator("#currentAddress").fill(currentAddr);
        page.locator("#permanentAddress").fill(permanentAddr);

        // Submit
        page.locator("#submit").click();

        Locator output = page.locator("#output");
        assertThat(output).isVisible();
        assertThat(output).containsText(name);
        assertThat(output).containsText(email);

        assertThat(page).hasURL(Pattern.compile(".*/text-box.*"));
    }

    private static void assertUnder(Duration actual, Duration limit, String message) {
        assertTrue(actual.compareTo(limit) < 0, message + " (actual=" + actual.toMillis() + "ms, limit=" + limit.toMillis() + "ms)");
    }
}
