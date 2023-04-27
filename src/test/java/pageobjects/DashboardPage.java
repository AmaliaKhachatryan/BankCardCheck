package pageobjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import dataclient.ClientInfo;

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

    public DashboardPage shouldBeErrorMessage() {
        errorMessage.shouldHave(Condition.exactText("Ошибка! Произошла ошибка"), Duration.ofSeconds(10));
        return this;
    }

    public ClientCardsPage clickButtonDeposit() {
        buttonDeposit.click();
        return new ClientCardsPage();
    }

    public ClientCardsPage makeATransfer(int transferAmount, ClientInfo.ClientCards card) {
        insetTransferAmount(transferAmount);
        insetCardNumber(card.getNumberCard());
        clickButtonDeposit();
        return new ClientCardsPage();
    }

    public ClientCardsPage makeATransfer(double transferAmount, ClientInfo.ClientCards card) {
        insetTransferAmount(transferAmount);
        insetCardNumber(card.getNumberCard());
        clickButtonDeposit();
        return new ClientCardsPage();
    }

    public ClientCardsPage makeATransferWithNullValueAmount(ClientInfo.ClientCards card) {
        insetCardNumber(card.getNumberCard());
        clickButtonDeposit();
        return new ClientCardsPage();
    }

    public DashboardPage getErrorMessageWhenInvalidCard(int transferAmount, String cardNumber) {
        insetTransferAmount(transferAmount);
        insetCardNumber(cardNumber);
        clickButtonDeposit();
        return this;
    }
    public DashboardPage makeATransferWithOverBalance(int transferAmount, ClientInfo.ClientCards card) {
        insetTransferAmount(transferAmount);
        insetCardNumber(card.getNumberCard());
        clickButtonDeposit();
        return this;
    }
}
