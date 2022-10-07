public class BankAccountTester {
    public static void main(String[] args) {
        try {
            BankAccount.login();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
