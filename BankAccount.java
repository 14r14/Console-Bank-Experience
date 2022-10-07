import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class BankAccount {
    private String accountId;
    private String accountName;
    private double balance;
    private String password;
    private static final double interestRate = 0.03;

    public BankAccount(String accountName, double balance, String password, boolean login, String accountId) {

        if (login) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.balance = balance;
            this.password = password;
        } else {
            this.accountId = UUID.randomUUID().toString();
            this.accountName = accountName;
            this.balance = balance;
            this.password = password;
        }

        try {
            if (login) {
                return;
            } else {
                File file = new File("accounts.txt");
                PrintWriter writer = new PrintWriter(new FileWriter("accounts.txt", true));
                if (file.createNewFile()) {
                    writer.append(accountId + "," + accountName + "," + balance + "," + password + "\n");
                } else {
                    writer.append(accountId + "," + accountName + "," + balance + "," + password + "\n");
                }
                writer.close();
                System.out.println("Account created successfully.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static BankAccount createAccount() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter account name: ");
        String accountName = scanner.nextLine();

        System.out.println("Enter initial balance: ");
        double balance = scanner.nextDouble();

        char[] pwd = System.console().readPassword("Enter password: ");

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(pwd, salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return new BankAccount(accountName, balance, hash.toString(), false, null);
    }

    private static boolean checkPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] pwd = password.toCharArray();

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(pwd, salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return password.equals(hash.toString());
    }

    public static BankAccount login() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter account id: ");
        String accountId = scanner.nextLine();

        char[] pwd = System.console().readPassword("Enter password: ");

        File accountFile = new File("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        if (BankAccount.checkPassword(pwd.toString())) {
            String[] accountInfo = new String[4];
            while (fileScanner.hasNextLine()) {
                accountInfo = fileScanner.nextLine().split(",");
                if (accountInfo[0].equals(accountId)) {
                    break;
                }
            }
            return new BankAccount(accountInfo[1], Double.parseDouble(accountInfo[3]), accountInfo[3], true,
                    accountInfo[0]);
        } else {
            throw new Exception("Invalid account-id or password.");
        }
    }
}
