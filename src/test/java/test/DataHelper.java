package test;

import data.ClientInfo;
import org.junit.jupiter.api.Assertions;
import page.ClientCardsPage;

public class DataHelper {
    static int actualBalanceFirstCard;
    static int actualBalanceSecondCard;
    static int startBalanceFirstCard;
    static int startBalanceSecondCard;
    static Integer transferAmountInt;
    static double transferAmountDouble;
    static int expectedBalanceFirstCard;
    static int expectedBalanceSecondCard;
    static ClientCardsPage clientCardsPage = new ClientCardsPage();


    public static void getStartBalancesCardWithPrint(ClientInfo.ClientCards first, ClientInfo.ClientCards second) {
        startBalanceFirstCard = clientCardsPage.getCardBalance(first);
        startBalanceSecondCard = clientCardsPage.getCardBalance(second);
        System.out.println("Стартовый баланс первой карты: " + startBalanceFirstCard);
        System.out.println("Стартовый баланс второй карты: " + startBalanceSecondCard);
    }

    public static void getStartBalancesCards(ClientInfo.ClientCards first, ClientInfo.ClientCards second) {
        startBalanceFirstCard = clientCardsPage.getCardBalance(first);
        startBalanceSecondCard = clientCardsPage.getCardBalance(second);
    }

    public static void getTransferAmount(int amount) {
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

    public static void getActualBalancesCards(ClientInfo.ClientCards first, ClientInfo.ClientCards second) {
        actualBalanceFirstCard = clientCardsPage.getCardBalance(first);
        actualBalanceSecondCard = clientCardsPage.getCardBalance(second);
        System.out.println("Фактический баланс первой карты: " + actualBalanceFirstCard);
        System.out.println("Фактический баланс второй карты: " + actualBalanceSecondCard);
    }

    public static void assertBalancesCards() {
        Assertions.assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

}
