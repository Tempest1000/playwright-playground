package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightSmokeTests {
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
        // Headed for demo; switch to headless by .setHeadless(true)
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
    }

    @Test
    void homepageHasGetStartedLink() {
        page.navigate("https://playwright.dev/");
        Locator getStarted = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get started"));
        assertThat(getStarted).isVisible();
    }

    @Test
    void docsPageLoads() {
        page.navigate("https://playwright.dev/");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get started")).click();
        assertThat(page).hasURL("https://playwright.dev/docs/intro");
    }
}
