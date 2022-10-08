import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class BankAccount {
    private String accountId;
    private String accountName;
    private double balance;
    private String password;
    private static final double OVERDRAFT_FEE = 35.00;

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

    public static BankAccount login() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account id: ");
        String accountId = scanner.nextLine();

        System.out.print("Enter password: ");
        String pwd = scanner.nextLine();

        File accountFile = new File("accounts.txt");
        Path path = Paths.get("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        String[] accountInfo = new String[4];
        long numLines = 0;
        try {
            numLines = Files.lines(path).parallel().count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int liveLine = 0;
        while (fileScanner.hasNextLine()) {
            accountInfo = fileScanner.nextLine().split(",");
            liveLine++;
            if (accountId.equals(accountInfo[0])) {
                System.out.println("Account found. Checking password...");
                if (accountInfo[3].equals(pwd)) {
                    System.out.println("Logged in successfully as " + accountInfo[1] + ".");
                    System.out.println();
                    break;
                } else {
                    throw new Exception("Incorrect password.");
                }
            } else {
                if (liveLine >= numLines) {
                    throw new Exception("Account not found.");
                }
            }
        }
        return new BankAccount(accountInfo[1], Double.parseDouble(accountInfo[2]), accountInfo[3], true,
                accountInfo[0]);
    }

    private static String[] getFileData(String accountId) throws Exception {

        File accountFile = new File("accounts.txt");
        Path path = Paths.get("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        long numLines = 0;
        try {
            numLines = Files.lines(path).parallel().count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] accountInfo = new String[4];

        int liveLine = 0;
        while (fileScanner.hasNextLine()) {
            accountInfo = fileScanner.nextLine().split(",");
            liveLine++;
            if (accountId.equals(accountInfo[0])) {
                break;
            } else {
                if (liveLine >= numLines) {
                    throw new Exception("Account not found.");
                }
            }
        }
        return accountInfo;
    }

    private static void deposit(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        account.balance += amount;
        System.out.println("Deposit successful. New balance: " + account.balance);
    }

    private static void withdraw(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        if (amount > account.balance) {
            System.out.println("Insufficient funds. Overdraft fee of $" + OVERDRAFT_FEE + " applied.");
            account.balance -= OVERDRAFT_FEE;
            System.out.println("New balance: " + account.balance);
        } else {
            account.balance -= amount;
            System.out.println("Withdrawal successful. New balance: " + account.balance);
        }
    }

    private static void saveAllData(BankAccount account) throws Exception {
        File accountFile = new File("accounts.txt");
        Path path = Paths.get("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        StringBuffer buffer = new StringBuffer();

        while (fileScanner.hasNextLine()) {
            buffer.append(fileScanner.nextLine() + System.lineSeparator());
        }

        String fileContents = buffer.toString();
        String[] accountInfo = getFileData(account.accountId);

        fileContents = fileContents.replaceAll(
                accountInfo[0] + "," + accountInfo[1] + "," + accountInfo[2] + "," + accountInfo[3],
                account.accountId + "," + account.accountName + "," + account.balance + "," + account.password);
        FileWriter writer = new FileWriter("accounts.txt");
        writer.append(fileContents);
        writer.flush();
    }

    private static double getBalance(BankAccount account) {
        return account.balance;
    }

    private static String updateAccountName(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new account name: ");
        String newName = scanner.nextLine();
        System.out.print("Are you sure you want to change your account name to " + newName
                + "? This action cannot be reversed (y/n): ");
        boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
        if (confirm) {
            account.accountName = newName;
            System.out.println("Account name changed successfully.");
        } else {
            System.out.println("Account name change cancelled.");
        }
        return "Done";
    }

    private static String updatePassword(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter current password: ");
        String currentPwd = scanner.nextLine();
        if (currentPwd.equals(account.password)) {
            System.out.print("Enter new password: ");
            String newPwd = scanner.nextLine();
            System.out.print("Confirm new password: ");
            String confirmPwd = scanner.nextLine();
            if (newPwd.equals(confirmPwd)) {
                System.out.print("Are you sure you want to change your password" +
                        "? This action cannot be reversed (y/n): ");
                boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
                if (confirm) {
                    account.password = newPwd;
                    System.out.println("Password changed successfully.");
                } else {
                    System.out.println("Password change cancelled.");
                }
            } else {
                System.out.println("Passwords do not match. Password change cancelled.");
            }
        } else {
            System.out.println("Incorrect password. Password change cancelled.");
        }
        return "Done";
    }

    private static void deleteAccountFromFile(BankAccount account) throws Exception {
        File accountFile = new File("accounts.txt");
        Path path = Paths.get("accounts.txt");

        Scanner fileScanner = new Scanner(accountFile);

        StringBuffer buffer = new StringBuffer();

        while (fileScanner.hasNextLine()) {
            buffer.append(fileScanner.nextLine() + System.lineSeparator());
        }

        String fileContents = buffer.toString();
        String[] accountInfo = getFileData(account.accountId);

        fileContents = fileContents.replaceAll(
                accountInfo[0] + "," + accountInfo[1] + "," + accountInfo[2] + "," + accountInfo[3]
                        + System.lineSeparator(),
                "");
        FileWriter writer = new FileWriter("accounts.txt");
        writer.append(fileContents);
        writer.flush();
    }

    private static String deleteAccount(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter current password: ");
        String currentPwd = scanner.nextLine();
        if (currentPwd.equals(account.password)) {
            System.out.print("Are you sure you want to delete your account? This action cannot be reversed (y/n): ");
            boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
            if (confirm) {
                try {
                    deleteAccountFromFile(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Account deleted successfully.");
                return "Done4";
            } else {
                System.out.println("Account deletion cancelled.");
            }
        } else {
            System.out.println("Incorrect password. Account deletion cancelled.");
        }
        return "Done";
    }

    private static int accountInfoSettings(BankAccount account) {
        HashMap<Integer, String> settings = new HashMap<Integer, String>();

        settings.put(1, "View account info");
        settings.put(2, "Change account name");
        settings.put(3, "Change password");
        settings.put(4, "Delete account");
        settings.put(5, "Return to main menu");

        Scanner scanner = new Scanner(System.in);

        int choice = 0;
        while (true) {
            System.out.println("Account settings:");
            for (int i = 1; i <= settings.size(); i++) {
                System.out.println(i + ". " + settings.get(i));
            }
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice > 0 && choice <= settings.size()) {
                if (choice == 1) {
                    System.out.println("Account ID: " + account.accountId);
                    System.out.println("Account name: " + account.accountName);
                    System.out.println("Balance: " + account.balance);
                    System.out.println();
                    continue;
                } else if (choice == 2) {
                    updateAccountName(account);
                    continue;
                } else if (choice == 3) {
                    updatePassword(account);
                    continue;
                } else if (choice == 4) {
                    try {
                        String result = deleteAccount(account);
                        if (result.equals("Done4")) {
                            return 404;
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
        return choice;
    }

    private static int getChoice(BankAccount account) {
        HashMap<Integer, String> choices = new HashMap<Integer, String>();

        choices.put(1, "Deposit");
        choices.put(2, "Withdraw");
        choices.put(3, "Check Balance");
        choices.put(4, "View Account Info & Settings");
        choices.put(5, "Exit");

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.println("*****CAL BANK*****");
            System.out.println("What would you like to do?");
            System.out.println();
            for (int i = 1; i <= choices.size(); i++) {
                System.out.println(i + ". " + choices.get(i));
            }
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice >= 1 && choice <= choices.size()) {
                if (choice == 1) {
                    deposit(account);
                    continue;
                } else if (choice == 2) {
                    withdraw(account);
                    continue;
                } else if (choice == 3) {
                    double curBalance = getBalance(account);
                    System.out.println("Your account has: $" + curBalance);
                    continue;
                } else if (choice == 4) {
                    int result = accountInfoSettings(account);
                    if (result == 404) {
                        return 404;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Saving your data...");
        try {
            saveAllData(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Exited the bank.");
        System.out.println("Hope you had a satisfactory experience!");

        return choice;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to California Bank!");
        System.out.println("1. Create account");
        System.out.println("2. Login");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            try {
                BankAccount account = BankAccount.createAccount();
                getChoice(account);
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        } else if (choice == 2) {
            try {
                BankAccount account = BankAccount.login();
                getChoice(account);
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }
}
