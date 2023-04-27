package pageobjectstest;

import com.codeborne.selenide.Configuration;
import dataclient.ClientInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pageobjects.ClientCardsPage;
import pageobjects.LoginClientPage;

import static com.codeborne.selenide.Selenide.*;
import static dataclient.ClientInfo.ClientCards.*;
import static dataclient.ClientInfo.LoginInfo.getLoginInfo;
import static dataclient.ClientInfo.VerificationCode.getVerificationCode;

public class TransferTest {
    static ClientInfo.LoginInfo clientAuto = getLoginInfo();
    static ClientInfo.VerificationCode code = getVerificationCode(clientAuto);
    static LoginClientPage loginClientPage;
    static ClientCardsPage clientCardsPage;
    static int actualBalanceFirstCard;
    static int actualBalanceSecondCard;
    static int startBalanceFirstCard;
    static int startBalanceSecondCard;
    static int transferAmountInt;
    static double transferAmountDouble;
    static int expectedBalanceFirstCard;
    static int expectedBalanceSecondCard;


    public static void authorizationClient() {
        open("http://localhost:9999");
        loginClientPage.clientLogin(clientAuto)
                .passVerification(code);
    }
    public static void makeATransferIntAmountFromSecondCardToFirst() {
        clientCardsPage.replenishFistCard()
                .makeATransfer(transferAmountInt, secondCard(clientAuto));
    }
    public static void makeATransferAmountFromFirstCardToSecond() {
        clientCardsPage.replenishSecondCard()
                .makeATransfer(transferAmountInt, firstCard(clientAuto));
    }
    public static void getStartBalancesCardWithPrint() {
        startBalanceFirstCard = clientCardsPage.getFirstCardBalance();
        startBalanceSecondCard = clientCardsPage.getSecondCardBalance();
        System.out.println("Стартовый баланс первой карты: " + startBalanceFirstCard);
        System.out.println("Стартовый баланс второй карты: " + startBalanceSecondCard);
    }

    public static void getStartBalancesCards() {
        startBalanceFirstCard = clientCardsPage.getFirstCardBalance();
        startBalanceSecondCard = clientCardsPage.getSecondCardBalance();
    }

    public static void getTransferIntAmount(int amount) {
        transferAmountInt = (int) Math.floor(Math.random() * (amount + 1));
        System.out.println("Сумма перевода: " + transferAmountInt);
    }

    public static void getTransferDoubleAmount(double amount) {
        transferAmountDouble = Math.floor(Math.random() * (amount + 1));
        System.out.println("Сумма перевода: " + transferAmountDouble);
    }

    public static void getExpectedBalancesCardToSecondFromFirst() {
        expectedBalanceFirstCard = startBalanceFirstCard - transferAmountInt;
        expectedBalanceSecondCard = startBalanceSecondCard + transferAmountInt;
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
    }

    public static void getExpectedBalancesCardToFirstFromSecond() {
        expectedBalanceFirstCard = startBalanceFirstCard + transferAmountInt;
        expectedBalanceSecondCard = startBalanceSecondCard - transferAmountInt;
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
    }

    public static void getActualBalancesCards() {
        actualBalanceFirstCard = clientCardsPage.getFirstCardBalance();
        actualBalanceSecondCard = clientCardsPage.getSecondCardBalance();
        System.out.println("Фактический баланс первой карты: " + actualBalanceFirstCard);
        System.out.println("Фактический баланс второй карты: " + actualBalanceSecondCard);
    }

    public static void assertBalancesCards() {
        Assertions.assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    @BeforeEach
    public void setUp() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        loginClientPage = new LoginClientPage();
        clientCardsPage = new ClientCardsPage();
        loginClientPage.clientLogin(clientAuto)
                .passVerification(code);
        getStartBalancesCards();
        if (startBalanceFirstCard == 0) {
            clientCardsPage.replenishFistCard().makeATransfer(firstCard(clientAuto).getBalance(), secondCard(clientAuto));
        }
        if (startBalanceSecondCard == 0) {
            clientCardsPage.replenishSecondCard().makeATransfer(secondCard(clientAuto).getBalance(), firstCard(clientAuto));
        }
        if (startBalanceFirstCard < 0) {
            transferAmountInt = -(startBalanceFirstCard) + firstCard(clientAuto).getBalance();
            makeATransferIntAmountFromSecondCardToFirst();
        }
        if (startBalanceSecondCard < 0) {
            transferAmountInt = -(startBalanceSecondCard) + secondCard(clientAuto).getBalance();
            makeATransferAmountFromFirstCardToSecond();
        }
        authorizationClient();
        getStartBalancesCardWithPrint();
    }
    @Test
    public void transferValidAmountFromSecondCardToFirst() {
        getTransferIntAmount(startBalanceSecondCard);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferIntAmountFromSecondCardToFirst();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferFromSecondCardWhitIncorrectCardNumber() {
        getTransferIntAmount(startBalanceSecondCard);
        getExpectedBalancesCardToFirstFromSecond();
        clientCardsPage.replenishFistCard()
                .getErrorMessageWhenInvalidCard(transferAmountInt, RandomStringUtils.randomNumeric(16))
                .shouldBeErrorMessage();
    }
    @Test
    public void transferNullValueFromSecondCardToFirst() {
        clientCardsPage.replenishFistCard().makeATransferWithNullValueAmount(secondCard(clientAuto));
        getActualBalancesCards();
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueSecondCardToFirst() {
        getTransferIntAmount(0);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferIntAmountFromSecondCardToFirst();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountFromSecondCardToFirst() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferIntAmountFromSecondCardToFirst();
        getActualBalancesCards();
        assertBalancesCards();
    }
    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceSecondCard() {
        transferAmountInt = (int) (startBalanceFirstCard + Math.random() * 99999);
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToFirstFromSecond();
        clientCardsPage.replenishFistCard()
                .makeATransferWithOverBalance(transferAmountInt, secondCard(clientAuto))
                .shouldBeErrorMessage();
    }
    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceSecondCard() {
        transferAmountInt = (int) (startBalanceFirstCard + Math.random() * 99999);
        System.out.println(transferAmountInt);
        clientCardsPage.replenishFistCard()
                .makeATransferWithOverBalance(transferAmountInt, secondCard(clientAuto));
        getActualBalancesCards();
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);

    }
    @Test
    public void checkErrorMessageWhenTransferFromSecondCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        makeATransferIntAmountFromSecondCardToFirst();
        getExpectedBalancesCardToFirstFromSecond();
        getTransferIntAmount(1000);
        authorizationClient();
        clientCardsPage.replenishFistCard()
                .makeATransferWithOverBalance(transferAmountInt, secondCard(clientAuto))
                .shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromSecondCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " +transferAmountInt);
        makeATransferIntAmountFromSecondCardToFirst();
        getExpectedBalancesCardToFirstFromSecond();
        authorizationClient();
        getTransferIntAmount(1000);
        makeATransferIntAmountFromSecondCardToFirst();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromSecondCardToFirstTest() {
        getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard + transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard - transferAmountDouble);
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
        clientCardsPage.replenishFistCard().makeATransfer(transferAmountDouble, secondCard(clientAuto));
        getActualBalancesCards();
        assertBalancesCards();
    }
    @Test
    public void transferValidAmountFromFirstCardToSecond() {
        getTransferIntAmount(startBalanceSecondCard);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountFromFirstCardToSecond();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferFromFirstCardWhitIncorrectCardNumber() {
        getTransferIntAmount(startBalanceSecondCard);
        getExpectedBalancesCardToSecondFromFirst();
        clientCardsPage.replenishSecondCard()
                .getErrorMessageWhenInvalidCard(transferAmountInt, RandomStringUtils.randomNumeric(16))
                .shouldBeErrorMessage();
    }
    @Test
    public void transferNullValueFromFirstCardToSecond() {
        clientCardsPage.replenishFistCard().makeATransferWithNullValueAmount(firstCard(clientAuto));
        getActualBalancesCards();
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueFromFirstCardToSecond() {
        getTransferIntAmount(0);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountFromFirstCardToSecond();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountFromFirstCardToSecond() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountFromFirstCardToSecond();
        getActualBalancesCards();
        assertBalancesCards();
    }
    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceFirstCard() {
        transferAmountInt = (int) (startBalanceFirstCard + Math.random() * 99999);
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToFirstFromSecond();
        clientCardsPage.replenishSecondCard()
                .makeATransferWithOverBalance(transferAmountInt, firstCard(clientAuto))
                .shouldBeErrorMessage();
    }
    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceFirstCard() {
        transferAmountInt = (int) (startBalanceFirstCard + Math.random() * 99999);
        System.out.println(transferAmountInt);
        clientCardsPage.replenishSecondCard()
                .makeATransferWithOverBalance(transferAmountInt, firstCard(clientAuto));
        getActualBalancesCards();
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);

    }
    @Test
    public void checkErrorMessageWhenTransferFromFirstCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountFromFirstCardToSecond();
        getTransferIntAmount(1000);
        authorizationClient();
        clientCardsPage.replenishSecondCard()
                .makeATransferWithOverBalance(transferAmountInt, firstCard(clientAuto))
                .shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromFirstCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " +transferAmountInt);
        makeATransferAmountFromFirstCardToSecond();
        getExpectedBalancesCardToSecondFromFirst();
        authorizationClient();
        getTransferIntAmount(1000);
        makeATransferAmountFromFirstCardToSecond();
        getActualBalancesCards();
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromFirstCardToSecondTest() {
        getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard - transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard + transferAmountDouble);
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
        clientCardsPage.replenishSecondCard().makeATransfer(transferAmountDouble, firstCard(clientAuto));
        getActualBalancesCards();
        assertBalancesCards();
    }
}


