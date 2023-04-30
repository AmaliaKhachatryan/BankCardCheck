package data;


import lombok.Value;

public class ClientInfo {
    private ClientInfo() {
    }

    public static LoginInfo getLoginInfo() {
        return new LoginInfo("vasya", "qwerty123");
    }

    public static VerificationCode getVerificationCode(LoginInfo loginInfo) {
        return new VerificationCode(12345);
    }

    public static ClientCards firstCard(LoginInfo loginInfo) {
        return new ClientCards("5559000000000001", "92df3f1c-a033-48e6-8390-206f6b1f56c0");
    }

    public static ClientCards secondCard(LoginInfo loginInfo) {
        return new ClientCards("5559000000000002", "0f3f5c2a-249e-4c3d-8287-09f7a039391d");
    }

    @Value
    public static class LoginInfo {
        private String login;
        private String password;
    }

    @Value
    public static class VerificationCode {
        private int verificationCode;
    }

    @Value
    public static class ClientCards {
        private String numberCard;
        private String testId;
    }
}

