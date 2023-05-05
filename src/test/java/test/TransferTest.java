package test;

import com.codeborne.selenide.Configuration;
import data.ClientInfo;
import data.DataHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.ClientCardsPage;
import page.DashboardPage;
import page.LoginClientPage;

import static com.codeborne.selenide.Selenide.*;
import static data.ClientInfo.*;


public class TransferTest {
    ClientInfo.LoginInfo clientAuto = getLoginInfo();
    ClientInfo.VerificationCode code = getVerificationCode(clientAuto);
    LoginClientPage loginClientPage;
    DashboardPage dashboardPage;
    DataHelper dataHelper = new DataHelper();
    ClientCardsPage clientCardsPage;
    int actualBalanceFirstCard;
    int actualBalanceSecondCard;
    int startBalanceFirstCard;
    int startBalanceSecondCard;
    int expectedBalanceFirstCard;
    int expectedBalanceSecondCard;
    int transferAmountInt;

    public void getStartBalancesCards(ClientInfo.ClientCards first, ClientInfo.ClientCards second) {
        startBalanceFirstCard = clientCardsPage.getCardBalance(first);
        startBalanceSecondCard = clientCardsPage.getCardBalance(second);
    }

    public void getExpectedBalancesFirstCardPlusAmount() {
        expectedBalanceFirstCard = startBalanceFirstCard + transferAmountInt;
    }

    public void getExpectedBalancesSecondCardMinusAmount() {
        expectedBalanceSecondCard = startBalanceSecondCard - transferAmountInt;
    }

    public void getActualBalancesCards(ClientInfo.ClientCards first, ClientInfo.ClientCards second) {
        actualBalanceFirstCard = clientCardsPage.getCardBalance(first);
        actualBalanceSecondCard = clientCardsPage.getCardBalance(second);
    }

    public void assertBalancesCards() {
        Assertions.assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    public void authorizationClient() {
        open("http://localhost:9999");
        loginClientPage.clientLogin(clientAuto)
                .passVerification(code);
    }

    public void makeATransferAmountToFrom(ClientCards toCard, ClientCards fromCard) {
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
        clientCardsPage = new ClientCardsPage();
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
    }
    @Test
    public void transferValidAmountFromSecondCardToFirst() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(startBalanceSecondCard);
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFromSecondCardWhitIncorrectCardNumber() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(startBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransfer(transferAmountInt, RandomStringUtils.randomNumeric(16));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }
    @Test
    public void transferNullValueFromSecondCardToFirst() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransferWithoutAmount(secondCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueSecondCardToFirst() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(0);
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountFromSecondCardToFirst() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = startBalanceSecondCard;
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }
    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceSecondCard() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceSecondCard() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void checkErrorMessageWhenTransferFromSecondCardHaveZeroValueBalance() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = startBalanceSecondCard;
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        transferAmountInt = dataHelper.getTransferAmount(1000);
        authorizationClient();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromSecondCardHaveZeroValueBalance() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        transferAmountInt = startBalanceSecondCard;
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        authorizationClient();
        transferAmountInt = dataHelper.getTransferAmount(1000);
        makeATransferAmountToFrom(firstCard(clientAuto), secondCard(clientAuto));
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromSecondCardToFirstTest() {
        getStartBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        var transferAmountDouble = dataHelper.getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard + transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard - transferAmountDouble);
        clientCardsPage.buttonReplenishCard(firstCard(clientAuto))
                .makeATransferDoubleAmount(transferAmountDouble, secondCard(clientAuto).getNumberCard());
        getActualBalancesCards(firstCard(clientAuto), secondCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferValidAmountToSecondCardFromFirst() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(startBalanceSecondCard);
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFromFirstCardWhitIncorrectCardNumber() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(startBalanceSecondCard);
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransfer(transferAmountInt, RandomStringUtils.randomNumeric(16));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void transferNullValueFromFirstCardToSecond() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransferWithoutAmount(firstCard(clientAuto).getNumberCard());
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    public void transferZeroValueFromFirstCardToSecond() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = dataHelper.getTransferAmount(0);
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferTotalAmountToSecondFromFirstCard() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = startBalanceSecondCard;
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void checkErrorMessageWhenTransferAmountExceedsBalanceFirstCard() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferAmountExceedsBalanceFirstCard() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = (int) ((startBalanceFirstCard + 1) + Math.random() * 99);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        Assertions.assertEquals(startBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(startBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test//
    public void checkErrorMessageWhenTransferFromFirstCardHaveZeroValueBalance() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = startBalanceFirstCard;
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        transferAmountInt = dataHelper.getTransferAmount(1000);
        authorizationClient();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        dashboardPage = new DashboardPage();
        dashboardPage.shouldBeErrorMessage();
    }

    @Test
    public void checkBalanceWhenTransferFromFirstCardHaveZeroValueBalance() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        transferAmountInt = startBalanceFirstCard;
        getExpectedBalancesFirstCardPlusAmount();
        getExpectedBalancesSecondCardMinusAmount();
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        authorizationClient();
        transferAmountInt = dataHelper.getTransferAmount(1000);
        makeATransferAmountToFrom(secondCard(clientAuto), firstCard(clientAuto));
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        assertBalancesCards();
    }

    @Test
    public void transferFractionalAmountFromFirstCardToSecondTest() {
        getStartBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        var transferAmountDouble = dataHelper.getTransferDoubleAmount(99.99);
        expectedBalanceFirstCard = (int) (startBalanceFirstCard - transferAmountDouble);
        expectedBalanceSecondCard = (int) (startBalanceSecondCard + transferAmountDouble);
        clientCardsPage.buttonReplenishCard(secondCard(clientAuto))
                .makeATransferDoubleAmount(transferAmountDouble, firstCard(clientAuto).getNumberCard());
        getActualBalancesCards(secondCard(clientAuto), firstCard(clientAuto));
        assertBalancesCards();
    }
}


