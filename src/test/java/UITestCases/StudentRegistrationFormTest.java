package UITestCases;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.StudentRegistrationPage;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Student Registration Form - demoqa.com")
class StudentRegistrationFormTest extends BaseTest {

    private static final Path UPLOAD_FILE =
            Paths.get("src", "test", "resources", "sample-upload.txt").toAbsolutePath();

    @Test
    @DisplayName("Filling out the form, uploading the file, selecting the date/dropdowns and successfully submitting")
    void shouldSubmitFormSuccessfullyWithAllFields() {
        StudentRegistrationPage form = new StudentRegistrationPage(page).open();

        String firstName = "Ivan";
        String lastName = "Petrov";
        String email = "ivan.petrov@example.com";
        String gender = "Male";
        String mobile = "9998887766";
        String day = "15";
        String month = "May";
        String year = "1995";
        String subject = "Maths";
        String hobby = "Reading";
        String address = "123 Test Street, Chisinau";
        String state = "NCR";
        String city = "Delhi";

        form.fillFirstName(firstName)
                .fillLastName(lastName)
                .fillEmail(email)
                .selectGender(gender)
                .fillMobile(mobile)
                .setDateOfBirth(day, month, year)
                .selectSubject(subject)
                .selectHobby(hobby)
                .uploadFile(UPLOAD_FILE)
                .fillCurrentAddress(address)
                .selectState(state)
                .selectCity(city);

        form.submit();

        assertThat(form.isModalVisible()).isTrue();
        assertThat(form.getModalTitleText()).containsIgnoringCase("Thanks for submitting the form");

        assertThat(form.getModalFieldValue("Student Name")).isEqualTo(firstName + " " + lastName);
        assertThat(form.getModalFieldValue("Student Email")).isEqualTo(email);
        assertThat(form.getModalFieldValue("Gender")).isEqualTo(gender);
        assertThat(form.getModalFieldValue("Mobile")).isEqualTo(mobile);
        assertThat(form.getModalFieldValue("Date of Birth")).contains(day, year);
        assertThat(form.getModalFieldValue("Subjects")).contains(subject);
        assertThat(form.getModalFieldValue("Hobbies")).contains(hobby);
        assertThat(form.getModalFieldValue("Picture")).isEqualTo("sample-upload.txt");
        assertThat(form.getModalFieldValue("Address")).isEqualTo(address);
        assertThat(form.getModalFieldValue("State and City")).isEqualTo(state + " " + city);

        form.closeModal();
    }

    @Test
    @DisplayName("Negative: Attempting to submit an empty form does not open the success modal.")
    void shouldNotShowSuccessModalWhenRequiredFieldsAreMissing() {
        StudentRegistrationPage form = new StudentRegistrationPage(page).open();

        form.submit();

        assertThat(form.isModalVisible()).isFalse();
    }

    @Test
    @DisplayName("The date of birth is correctly displayed in the modal after selection in the date picker.")
    void shouldSelectDateOfBirthCorrectly() {
        StudentRegistrationPage form = new StudentRegistrationPage(page).open();

        form.fillFirstName("Anna")
                .fillLastName("Sidorova")
                .fillEmail("anna.sidorova@example.com")
                .selectGender("Female")
                .fillMobile("9123456780")
                .setDateOfBirth("3", "January", "2000")
                .submit();

        assertThat(form.isModalVisible()).isTrue();
        assertThat(form.getModalFieldValue("Date of Birth")).isEqualTo("03 January,2000");

        form.closeModal();
    }
}
