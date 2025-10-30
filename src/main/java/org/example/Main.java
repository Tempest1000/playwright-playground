package org.example;

import java.util.regex.Pattern;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import java.nio.file.Paths; // for screenshot directory

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class Main {
    public static void main(String[] args) {
        //firefoxTest();
        chromiumTest();
    }

    private static void firefoxTest() {
        try (Playwright playwright = Playwright.create()) {
            //Browser browser = playwright.chromium().launch();
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50));
            Page page = browser.newPage();
            page.navigate("https://playwright.dev");
            System.out.println(page.title());
            //page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
        }
    }

    private static void chromiumTest() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://playwright.dev");

            assertThat(page).hasTitle(Pattern.compile("Playwright"));

            Locator getStarted = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get Started"));

            assertThat(getStarted).hasAttribute("href", "/docs/intro");

            getStarted.click();

            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));

            assertThat(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Installation"))).isVisible();
        }
    }

}