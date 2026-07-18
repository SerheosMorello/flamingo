package pages;

import base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.nio.file.Path;
import io.github.cdimascio.dotenv.Dotenv;

public class StudentRegistrationPage extends BasePage {
    static Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("BASE_URL");

    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator emailInput;
    private final Locator mobileInput;
    private final Locator dateOfBirthInput;
    private final Locator subjectsInput;
    private final Locator currentAddressInput;
    private final Locator stateDropdown;
    private final Locator cityDropdown;
    private final Locator uploadPictureInput;
    private final Locator submitButton;

    private final Locator modalTitle;
    private final Locator modalTable;
    private final Locator modalCloseButton;

    public StudentRegistrationPage(Page page) {
        super(page);
        this.firstNameInput = page.locator("#firstName");
        this.lastNameInput = page.locator("#lastName");
        this.emailInput = page.locator("#userEmail");
        this.mobileInput = page.locator("#userNumber");
        this.dateOfBirthInput = page.locator("#dateOfBirthInput");
        this.subjectsInput = page.locator("#subjectsInput");
        this.currentAddressInput = page.locator("#currentAddress");
        this.stateDropdown = page.locator("#state");
        this.cityDropdown = page.locator("#city");
        this.uploadPictureInput = page.locator("#uploadPicture");
        this.submitButton = page.locator("#submit");

        this.modalTitle = page.locator("#example-modal-sizes-title-lg");
        this.modalTable = page.locator(".table-responsive");
        this.modalCloseButton = page.locator("#closeLargeModal");
    }


    /** Открыть страницу и дождаться её полной готовности. */
    public StudentRegistrationPage open() {
        page.navigate(URL);
        waitForPageLoaded();
        waitVisible(firstNameInput);
        return this;
    }

    public StudentRegistrationPage fillFirstName(String value) {
        waitVisible(firstNameInput);
        firstNameInput.fill(value);
        return this;
    }

    public StudentRegistrationPage fillLastName(String value) {
        lastNameInput.fill(value);
        return this;
    }

    public StudentRegistrationPage fillEmail(String value) {
        emailInput.fill(value);
        return this;
    }

    public StudentRegistrationPage selectGender(String gender) {
        Locator label = page.locator("//div//label[text()='" + gender + "']");
        waitVisible(label);
        label.click();
        return this;
    }

    public StudentRegistrationPage fillMobile(String value) {
        mobileInput.fill(value);
        return this;
    }

    public StudentRegistrationPage setDateOfBirth(String day, String month, String year) {
        dateOfBirthInput.click();
        Locator datepicker = page.locator(".react-datepicker");
        waitVisible(datepicker);

        Locator monthSelect = datepicker.locator(".react-datepicker__month-select");
        Locator yearSelect = datepicker.locator(".react-datepicker__year-select");
        monthSelect.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(month));
        yearSelect.selectOption(year);

        Locator dayCell = datepicker.locator(
                        "div.react-datepicker__day:not(.react-datepicker__day--outside-month)")
                .filter(new Locator.FilterOptions().setHasText(day))
                .first();
        waitVisible(dayCell);
        dayCell.click();

        // datepicker должен закрыться после выбора дня
        waitVisible(dateOfBirthInput);
        return this;
    }

    public StudentRegistrationPage selectSubject(String subject) {
        subjectsInput.click();
        subjectsInput.fill(subject);
        Locator option = page.locator("#subjectsContainer .subjects-auto-complete__option").first();
        waitVisible(option);
        option.click();
        return this;
    }

    public StudentRegistrationPage selectHobby(String hobby) {
        Locator label = page.locator("div#hobbiesWrapper label")
                .filter(new Locator.FilterOptions().setHasText(hobby));
        waitVisible(label);
        label.click();
        return this;
    }

    public StudentRegistrationPage uploadFile(Path filePath) {
        uploadPictureInput.setInputFiles(filePath);
        return this;
    }

    public StudentRegistrationPage fillCurrentAddress(String value) {
        currentAddressInput.fill(value);
        return this;
    }

    public StudentRegistrationPage selectState(String state) {
        scrollIntoView(stateDropdown);
        stateDropdown.click();
        Locator option = page.locator("div[id^='react-select-3-option']")
                .filter(new Locator.FilterOptions().setHasText(state));
        waitVisible(option);
        option.click();
        return this;
    }

    public StudentRegistrationPage selectCity(String city) {
        scrollIntoView(cityDropdown);
        cityDropdown.click();
        Locator option = page.locator("div[id^='react-select-4-option']")
                .filter(new Locator.FilterOptions().setHasText(city));
        waitVisible(option);
        option.click();
        return this;
    }

    public void submit() {
        //AdBlockerUtils.removeAdBanners(page);
        scrollIntoView(submitButton);
        submitButton.click();
    }


    public Locator getModalTitle() {
        waitVisible(modalTitle);
        return modalTitle;
    }

    public String getModalTitleText() {
        waitVisible(modalTitle);
        return modalTitle.innerText();
    }

    public String getModalFieldValue(String labelText) {
        waitVisible(modalTable);
        Locator row = modalTable.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(labelText));
        return row.locator("td").nth(1).innerText();
    }

    public void closeModal() {
        modalCloseButton.click();
    }

    public boolean isModalVisible() {
        return modalTitle.isVisible();
    }
}
