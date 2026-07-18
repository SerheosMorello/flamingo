package base;

import com.microsoft.playwright.*;
import listeners.ScreenshotOnFailureExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    private static final ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserTL = new ThreadLocal<>();

    private static final Set<Browser> allBrowsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<Playwright> allPlaywrights = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void createContextAndPage() {
        if (browserTL.get() == null) {
            Playwright pw = Playwright.create();
            boolean headless = !"false".equalsIgnoreCase(System.getProperty("headed"));
            Browser br = pw.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo(0));
            playwrightTL.set(pw);
            browserTL.set(br);
            allPlaywrights.add(pw);
            allBrowsers.add(br);
        }

        context = browserTL.get().newContext(new Browser.NewContextOptions()
                .setViewportSize(1366, 900));
        context.setDefaultTimeout(10_000);
        page = context.newPage();
        ScreenshotOnFailureExtension.registerPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void closeBrowser() {
        allBrowsers.forEach(Browser::close);
        allPlaywrights.forEach(Playwright::close);
        allBrowsers.clear();
        allPlaywrights.clear();
    }
}