package dataclient;


import lombok.Value;

public class ClientInfo {
    private ClientInfo() {

    }

    @Value
    public static class LoginInfo {
        private String login;
        private String password;

        public static LoginInfo getLoginInfo() {
            return new LoginInfo("vasya", "qwerty123");
        }
    }

    @Value
    public static class VerificationCode {
        private int verificationCode;

        public static VerificationCode getVerificationCode(LoginInfo loginInfo) {
            return new VerificationCode(12345);
        }
    }

    @Value
    public static class ClientCards {
        private String numberCard;
        private int balance;
        public static ClientCards firstCard (LoginInfo loginInfo) {
            return new ClientCards("5559000000000001", 10000);
        }
        public static ClientCards secondCard (LoginInfo loginInfo) {
            return new ClientCards("5559000000000002", 10000);
        }
    }
}

