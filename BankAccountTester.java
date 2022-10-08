public class BankAccountTester {
    public static void main(String[] args) {
        try {
            BankAccount.login();
            // BankAccount.createAccount();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
