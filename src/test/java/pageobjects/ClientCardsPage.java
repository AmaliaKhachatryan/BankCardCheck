package pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ClientCardsPage {
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final SelenideElement headerClientCars = $(byText("Ваши карты"));
    private final ElementsCollection buttonsDeposit = $$("button[data-test-id='action-deposit']");

    public int getFirstCardBalance() {
        String text = cards.first().text();
        return extractBalance(text);
    }

    public int getSecondCardBalance() {
        String text = cards.last().text();
        return extractBalance(text);
    }

    public int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public ClientCardsPage() {
    }

    public DashboardPage replenishFistCard() {
        buttonsDeposit.get(0).click();
        return new DashboardPage();
    }

    public DashboardPage replenishSecondCard() {
        buttonsDeposit.get(1).click();
        return new DashboardPage();
    }
}
