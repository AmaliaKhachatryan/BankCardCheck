package test;

import com.codeborne.selenide.Configuration;
import data.ClientInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.DashboardPage;
import page.LoginClientPage;

import static com.codeborne.selenide.Selenide.*;
import static data.ClientInfo.*;
import static test.DataHelper.*;


public class TransferTest {
    static ClientInfo.LoginInfo clientAuto = getLoginInfo();
    static ClientInfo.VerificationCode code = getVerificationCode(clientAuto);
    static LoginClientPage loginClientPage;
    static DashboardPage dashboardPage;

    public static void authorizationClient() {
        open("http://localhost:9999");
        loginClientPage.clientLogin(clientAuto)
                .passVerification(code);
    }

    public static void makeATransferAmountToFrom(ClientCards toCard, ClientCards fromCard) {
        clientCardsPage.buttonReplenishCard(toCard)
                .makeATransfer(transferAmountInt, fromCard.getNumberCard());
    }

    @BeforeEach
    public void setUp() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        loginClientPage = new LoginClientPage();
        loginClientPage.clientLogin(clientAuto)
                .passVerification(code);
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        if (startBalanceFirstCard == 0) {
            transferAmountInt = startBalanceSecondCard / 2;
            makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        }
        if (startBalanceSecondCard == 0) {
            transferAmountInt = startBalanceFirstCard / 2;
            makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        }
        if (startBalanceFirstCard < 0) {
            transferAmountInt = (startBalanceSecondCard + startBalanceFirstCard) / 2 - startBalanceFirstCard;
            makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        }
        if (startBalanceSecondCard < 0) {
            transferAmountInt = (startBalanceFirstCard + startBalanceSecondCard) / 2 - startBalanceSecondCard;
            makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        }
        authorizationClient();
        getStartBalancesCardWithPrint(firstCard(clientAuto), secondCard(clientAuto));
    }
    @Test
    public void transferValidAmountFromSecondCardToFirst() {
        getTransferAmount(startBalanceSecondCard);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFromSecondCardWhitIncorrectCardNumber() {
        getTransferAmount(startBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransfer(transferAmountInt, RandomStringUtils.randomNumeric(16));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }
    @Test
    public void transferNullValueFromSecondCardToFirst() {
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransferWithoutAmount(secondCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueSecondCardToFirst() {
        getTransferAmount(0);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountFromSecondCardToFirst() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }
    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceSecondCard() {
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        System.out.println(transferAmountInt);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceSecondCard() {
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        System.out.println(transferAmountInt);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void checkErrorMessageWhenTransferFromSecondCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getExpectedBalancesCardToFirstFromSecond();
        getTransferAmount(1000);
        authorizationClient();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromSecondCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        getExpectedBalancesCardToFirstFromSecond();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        authorizationClient();
        getTransferAmount(1000);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromSecondCardToFirstTest() {
        getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard + transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard - transferAmountDouble);
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransferDoubleAmount(transferAmountDouble, secondCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferValidAmountToSecondCardFromFirst() {
        getTransferAmount(startBalanceSecondCard);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFromFirstCardWhitIncorrectCardNumber() {
        getTransferAmount(startBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransfer(transferAmountInt, RandomStringUtils.randomNumeric(16));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void transferNullValueFromFirstCardToSecond() {
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransferWithoutAmount(firstCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueFromFirstCardToSecond() {
        getTransferAmount(0);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountToSecondFromFirstCard() {
        transferAmountInt = startBalanceSecondCard;
        System.out.println(transferAmountInt);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceFirstCard() {
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        System.out.println(transferAmountInt);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceFirstCard() {
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        System.out.println(transferAmountInt);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test//
    public void checkErrorMessageWhenTransferFromFirstCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceFirstCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getExpectedBalancesCardToSecondFromFirst();
        getTransferAmount(1000);
        authorizationClient();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromFirstCardHaveZeroValueBalance() {
        transferAmountInt = startBalanceFirstCard;
        System.out.println("Сумма первого перевода: " + transferAmountInt);
        getExpectedBalancesCardToSecondFromFirst();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        authorizationClient();
        getTransferAmount(1000);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromFirstCardToSecondTest() {
        getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard - transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard + transferAmountDouble);
        System.out.println("Ожидаемый баланс первой карты: " + expectedBalanceFirstCard);
        System.out.println("Ожидаемый баланс второй карты: " + expectedBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransferDoubleAmount(transferAmountDouble, firstCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }
}


