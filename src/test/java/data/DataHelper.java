package data;

import lombok.Value;

public class DataHelper {
    private Integer transferAmountInt;
    private double transferAmountDouble;

    public int getTransferAmount(int amount) {
        transferAmountInt = (int) Math.floor(Math.random() * (amount + 1));
        return transferAmountInt;
    }

    public double getTransferDoubleAmount(double amount) {
        transferAmountDouble = Math.floor(Math.random() * (amount + 1));
        return transferAmountDouble;
    }
}
