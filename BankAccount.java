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
                    writer.append(this.accountId + "," + accountName + "," + balance + "," + password + "\n");
                } else {
                    writer.append(this.accountId + "," + accountName + "," + balance + "," + password + "\n");
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
        System.out.print("Enter account name: ");
        String accountName = scanner.nextLine();

        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter password: ");
        String pwd = scanner.nextLine();

        return new BankAccount(accountName, balance, pwd, false, null);
    }

    // private static boolean checkPassword(String password) throws
    // NoSuchAlgorithmException, InvalidKeySpecException {
    // String hashedPassword = new String(generateSecurePassword(password));

    // System.out.println("Hash to check against: " + hashedPassword);
    // return password.equals(hashedPassword);
    // }

    public static BankAccount login() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account id: ");
        String accountId = scanner.nextLine();

        System.out.print("Enter password: ");
        String pwd = scanner.nextLine();

        File accountFile = new File("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        String[] accountInfo = new String[4];
        while (fileScanner.hasNextLine()) {
            accountInfo = fileScanner.nextLine().split(",");
            if (accountInfo[0].equalsIgnoreCase(accountId)) {
                System.out.println("Account found. Checking password...");
                if (accountInfo[3].equals(pwd)) {
                    System.out.println("Logged in successfully as " + accountInfo[1] + ".");
                    break;
                } else {
                    throw new Exception("Incorrect password.");
                }
            } else {
                throw new Exception("Account not found.");
            }
        }
        return new BankAccount(accountInfo[1], Double.parseDouble(accountInfo[2]), accountInfo[3], true,
                accountInfo[0]);
    }
}
