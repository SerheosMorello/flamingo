package base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;


public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void waitForPageLoaded() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    protected void scrollIntoView(Locator locator) {
        locator.scrollIntoViewIfNeeded();
    }

    protected void waitVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }
}
