package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private final SelenideElement headerTopUpCard = $(byText("Пополнение карты"));
    private final SelenideElement amount = $("[data-test-id='amount'] input");
    private final SelenideElement numberCard = $("[data-test-id='from'] input");
    private final SelenideElement buttonDeposit = $("button[data-test-id='action-transfer']");
    private final SelenideElement errorMessage = $(byXpath(".//*[@class='notification__content']"));

    public DashboardPage() {
        headerTopUpCard.shouldBe(Condition.visible,Duration.ofSeconds(10));
    }

    public DashboardPage insetTransferAmount(int transferAmount) {
        amount.setValue(String.valueOf(transferAmount));
        return this;
    }

    public DashboardPage insetTransferAmount(double transferAmount) {
        amount.setValue(String.valueOf(transferAmount));
        return this;
    }

    public DashboardPage insetCardNumber(String cardNumber) {
        numberCard.setValue(cardNumber);
        return this;
    }

    public void shouldBeErrorMessage() {
        errorMessage.shouldHave(Condition.exactText("Ошибка! Произошла ошибка"), Duration.ofSeconds(10));
    }

    public void clickButtonDeposit() {
        buttonDeposit.click();
    }

    public void makeATransfer(int transferAmount, String numberCard) {
        insetTransferAmount(transferAmount);
        insetCardNumber(numberCard);
        clickButtonDeposit();
    }

    public void makeATransferDoubleAmount(double transferAmount, String numberCard) {
        insetTransferAmount(transferAmount);
        insetCardNumber(numberCard);
        clickButtonDeposit();
    }

    public void makeATransferWithoutAmount(String numberCard) {
        insetCardNumber(numberCard);
        clickButtonDeposit();
    }
}
