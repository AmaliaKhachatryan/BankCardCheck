package pageobjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import dataclient.ClientInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private final SelenideElement verificationCode = $("[data-test-id='code'] input");
    private final SelenideElement headerInternetBank = $(byText("Интернет Банк"));
    private final SelenideElement buttonContinue= $("button[data-test-id='action-verify']");

    public VerificationPage() {
        headerInternetBank.shouldBe(Condition.visible,Duration.ofSeconds(10));
    }

    public VerificationPage insertVerificationCode(int code) {
        verificationCode.setValue(String.valueOf(code));
        return this;
    }

    public ClientCardsPage clickButtonContinue() {
        buttonContinue.click();
        return new ClientCardsPage();
    }

    public ClientCardsPage passVerification(ClientInfo.VerificationCode code) {
        insertVerificationCode(code.getVerificationCode());
        clickButtonContinue();
        return new ClientCardsPage();
    }
}
