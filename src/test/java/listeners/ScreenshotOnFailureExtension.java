package listeners;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Optional;

public class ScreenshotOnFailureExtension implements TestWatcher {

    private static final ThreadLocal<Page> CURRENT_PAGE = new ThreadLocal<>();
    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");

    public static void registerPage(Page page) {
        CURRENT_PAGE.set(page);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Page page = CURRENT_PAGE.get();
        if (page == null) {
            return;
        }
        try {
            java.nio.file.Files.createDirectories(SCREENSHOT_DIR);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeName = context.getDisplayName().replaceAll("[^a-zA-Z0-9_-]", "_");
            Path screenshotPath = SCREENSHOT_DIR.resolve(safeName + "_" + timestamp + ".png");

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true)
                    .setType(ScreenshotType.PNG));

            System.out.println("Screenshot saved: " + screenshotPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    public static Optional<Page> getCurrentPage() {
        return Optional.ofNullable(CURRENT_PAGE.get());
    }
}
