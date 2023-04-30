package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.ClientInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class LoginClientPage {
    private final SelenideElement login = $("[data-test-id='login'] input");
    private final SelenideElement password = $("[data-test-id='password'] input");
    private final SelenideElement buttonContinue = $("button[data-test-id='action-login']");

    public LoginClientPage() {
        login.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public LoginClientPage insertLogin(String clientLogin) {
        login.setValue(clientLogin);
        return this;
    }

    public LoginClientPage insertPassword(String clientPassword) {
        password.setValue(clientPassword);
        return this;
    }

    public VerificationPage clickButtonContinue() {
        buttonContinue.click();
        return new VerificationPage();
    }

    public VerificationPage clientLogin(ClientInfo.LoginInfo loginInfo) {
        insertLogin(loginInfo.getLogin());
        insertPassword(loginInfo.getPassword());
        clickButtonContinue();
        return new VerificationPage();
    }
}