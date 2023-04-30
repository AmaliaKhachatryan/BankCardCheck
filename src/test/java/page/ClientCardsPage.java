package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.ClientInfo;
import lombok.val;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ClientCardsPage {
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final SelenideElement headerClientCars = $(byText("Ваши карты"));

    public int getCardBalance(ClientInfo.ClientCards card) {
        String text = $("[data-test-id='" + card.getTestId() + "']").getText();
        return extractBalance(text);
    }

    public int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public ClientCardsPage() {
        headerClientCars.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public DashboardPage buttonReplenishCard(ClientInfo.ClientCards card) {
        $("[data-test-id='" + card.getTestId() + "'] button").click();
        return new DashboardPage();
    }
}
