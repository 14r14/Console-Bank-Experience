import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class BankAccount {
    private String accountId;
    private String accountName;
    private double balance;
    private String password;

    private static final double OVERDRAFT_FEE = 35.00;

    public BankAccount(String accountName, double balance, String password, boolean loginOrCheck, String accountId) {

        if (loginOrCheck) {
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
            if (loginOrCheck) {
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
                System.out.println(ConsoleColors.colorize("Account created successfully.", ConsoleColors.GREEN));
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.colorize("An error occurred.", ConsoleColors.RED));
            e.printStackTrace();
        }
    }

    public static BankAccount createAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account name: ");
        String accountName = scanner.nextLine();

        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();

        char[] passwordEnter = System.console().readPassword("Enter password: ");
        String pwd = new String(passwordEnter);

        return new BankAccount(accountName, balance, pwd, false, null);
    }

    public static BankAccount login() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account id: ");
        String accountId = scanner.nextLine();

        char[] passwordEnter = System.console().readPassword("Enter password: ");
        String pwd = new String(passwordEnter);

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
                System.out.println(ConsoleColors.colorize("Account found. Checking password...", ConsoleColors.GREEN));
                if (accountInfo[3].equals(pwd)) {
                    System.out.println(ConsoleColors.colorize("Logged in successfully as " + accountInfo[1] + ".",
                            ConsoleColors.GREEN));
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
        System.out.println(
                ConsoleColors.colorize("Deposit successful. New balance: " + account.balance, ConsoleColors.GREEN));
    }

    private static void withdraw(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        if (amount > account.balance) {
            System.out.println(ConsoleColors.colorize(
                    "Insufficient funds. Overdraft fee of $" + OVERDRAFT_FEE + " applied.", ConsoleColors.RED));
            account.balance -= OVERDRAFT_FEE;
            System.out.println(ConsoleColors.colorize("New balance: " + account.balance, ConsoleColors.RED));
        } else {
            account.balance -= amount;
            System.out.println(ConsoleColors.colorize("Withdrawal successful. New balance: " + account.balance,
                    ConsoleColors.GREEN));
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
        System.out.print(ConsoleColors.colorize("Are you sure you want to change your account name to " + newName
                + "? This action cannot be reversed (y/n): ", ConsoleColors.YELLOW));
        boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
        if (confirm) {
            account.accountName = newName;
            System.out.println(ConsoleColors.colorize("Account name changed successfully.", ConsoleColors.GREEN));
        } else {
            System.out.println(ConsoleColors.colorize("Account name change cancelled.", ConsoleColors.RED));
        }
        return "Done";
    }

    private static String updatePassword(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        char[] passwordEnter = System.console().readPassword("Enter new password: ");
        String currentPwd = new String(passwordEnter);
        if (currentPwd.equals(account.password)) {
            System.out.print("Enter new password: ");
            String newPwd = scanner.nextLine();
            System.out.print("Confirm new password: ");
            String confirmPwd = scanner.nextLine();
            if (newPwd.equals(confirmPwd)) {
                System.out.print(ConsoleColors.colorize("Are you sure you want to change your password" +
                        "? This action cannot be reversed (y/n): ", ConsoleColors.YELLOW));
                boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
                if (confirm) {
                    account.password = newPwd;
                    System.out.println(ConsoleColors.colorize("Password changed successfully.", ConsoleColors.GREEN));
                } else {
                    System.out.println(ConsoleColors.colorize("Password change cancelled.", ConsoleColors.RED));
                }
            } else {
                System.out.println(ConsoleColors.colorize("Passwords do not match. Password change cancelled.",
                        ConsoleColors.RED));
            }
        } else {
            System.out.println(
                    ConsoleColors.colorize("Incorrect password. Password change cancelled.", ConsoleColors.RED));
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
        char[] passwordEnter = System.console().readPassword("Enter password: ");
        String currentPwd = new String(passwordEnter);
        if (currentPwd.equals(account.password)) {
            System.out.print(ConsoleColors.colorize(
                    "Are you sure you want to delete your account? This action cannot be reversed (y/n): ",
                    ConsoleColors.YELLOW));
            boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
            if (confirm) {
                try {
                    deleteAccountFromFile(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(ConsoleColors.colorize("Account deleted successfully.", ConsoleColors.GREEN));
                return "Done4";
            } else {
                System.out.println(ConsoleColors.colorize("Account deletion cancelled.", ConsoleColors.RED));
            }
        } else {
            System.out.println(
                    ConsoleColors.colorize("Incorrect password. Account deletion cancelled.", ConsoleColors.RED));
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
                System.out.println(ConsoleColors.colorize("Invalid choice. Please try again.", ConsoleColors.RED));
                continue;
            }
        }
        return choice;
    }

    private static void convertCurrency(BankAccount account) {
        String[] allowedLocales = Currency.LOCALES;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount to convert: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        if (amount > account.balance) {
            System.out.println(
                    ConsoleColors.colorize("You do not have enough funds to convert that amount.", ConsoleColors.RED));
        } else {
            System.out.println("Allowed currencies: ");
            for (int i = 0; i < allowedLocales.length; i++) {
                System.out.println(allowedLocales[i]);
            }
            System.out.print("Enter currency to convert to: ");
            String currency = scanner.nextLine();

            if (Arrays.asList(allowedLocales).contains(currency)) {
                System.out.println(ConsoleColors.colorize("Converting " + amount + " to " + currency + "...",
                        ConsoleColors.YELLOW));
                System.out.println("Current exchange rate: " + Currency.getLocaleCorrespondingExchangeRate(currency));
                account.balance -= amount;
                System.out.println(
                        ConsoleColors.colorize(
                                "Conversion complete. Converted amount: " + Currency.getCurrencySymbol(currency)
                                        + Currency.convert(currency, amount),
                                ConsoleColors.GREEN));
            } else {
                System.out.println(ConsoleColors.colorize("Currency conversion not available for your locale.",
                        ConsoleColors.RED));
            }
        }
    }

    private static void transferMoney(BankAccount account) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account ID to transfer to: ");
        String recAccountId = scanner.nextLine();
        String[] recAccountInfo;
        try {
            recAccountInfo = getFileData(recAccountId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        char[] passwordEnter = System.console().readPassword("Enter password: ");
        String pwd = new String(passwordEnter);
        if (pwd.equals(account.password)) {
            System.out.print("Enter amount to transfer: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            System.out.print(ConsoleColors.colorize(
                    "Are you sure you want to transfer " + amount + " to " + recAccountInfo[1] + "? (y/n): ",
                    ConsoleColors.YELLOW));
            boolean confirm = scanner.nextLine().equalsIgnoreCase("y");
            if (confirm) {
                if (amount > account.balance) {
                    System.out.println(ConsoleColors.colorize("You do not have enough funds to transfer that amount.",
                            ConsoleColors.RED));
                } else {
                    BankAccount recAccount = new BankAccount(recAccountInfo[1], Double.parseDouble(recAccountInfo[2]),
                            recAccountInfo[3], true, recAccountId);
                    System.out.println(ConsoleColors.colorize(
                            "Transferring " + amount + " to account " + recAccountInfo[1] + "...",
                            ConsoleColors.YELLOW));
                    account.balance -= amount;
                    recAccount.balance += amount;
                    try {
                        saveAllData(recAccount);
                        saveAllData(account);
                        System.out.println(ConsoleColors.colorize("Finalizing transfer...", ConsoleColors.YELLOW));
                        System.out.println(ConsoleColors.colorize("Transfer complete.", ConsoleColors.GREEN));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } else {
                System.out.println(ConsoleColors.colorize("Transfer cancelled.", ConsoleColors.RED));
                return;
            }
        } else {
            System.out.println(ConsoleColors.colorize("Incorrect password. Transfer cancelled.", ConsoleColors.RED));
            return;
        }
    }

    private static int getChoice(BankAccount account) {
        HashMap<Integer, String> choices = new HashMap<Integer, String>();

        choices.put(1, "Deposit");
        choices.put(2, "Withdraw");
        choices.put(3, "Check Balance");
        choices.put(4, "Convert Currency");
        choices.put(5, "Transfer Funds");
        choices.put(6, "View Account Info & Settings");
        choices.put(7, "Logout & Exit");

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.println(ConsoleColors.colorize("*****CAL BANK*****", ConsoleColors.BLUE_BOLD_BRIGHT));
            System.out.println(ConsoleColors.colorize("What would you like to do?", ConsoleColors.PURPLE_UNDERLINED));
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
                    System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        continue;
                    }
                } else if (choice == 2) {
                    withdraw(account);
                    System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        continue;
                    }
                } else if (choice == 3) {
                    double curBalance = getBalance(account);
                    System.out.println(
                            "Your account has: " + ConsoleColors.colorize("$" + curBalance, ConsoleColors.GREEN));
                    System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        continue;
                    }
                } else if (choice == 4) {
                    convertCurrency(account);
                    System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        continue;
                    }
                } else if (choice == 5) {
                    transferMoney(account);
                    System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        continue;
                    }
                } else if (choice == 6) {
                    int result = accountInfoSettings(account);
                    if (result == 404) {
                        return 404;
                    } else {
                        System.out.print(ConsoleColors.colorize("Done viewing? (y): ", ConsoleColors.YELLOW));
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("y")) {
                            continue;
                        }
                    }
                } else {
                    break;
                }
            } else {
                System.out.println(ConsoleColors.colorize("Invalid choice. Please try again.", ConsoleColors.RED));
            }
        }
        System.out.println(ConsoleColors.colorize("Saving your data...", ConsoleColors.YELLOW));
        try {
            saveAllData(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ConsoleColors.colorize("Exited the bank.", ConsoleColors.GREEN));
        System.out.println(ConsoleColors.colorize("Hope you had a satisfactory experience!",
                ConsoleColors.BLACK_BACKGROUND_BRIGHT));

        return choice;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(ConsoleColors.colorize("Welcome to California Bank!", ConsoleColors.BLUE_BOLD_BRIGHT));
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
                System.out.println(ConsoleColors.colorize("An error occurred.", ConsoleColors.RED));
                e.printStackTrace();
            }

        } else if (choice == 2) {
            try {
                BankAccount account = BankAccount.login();
                getChoice(account);
            } catch (Exception e) {
                System.out.println(ConsoleColors.colorize("An error occurred.", ConsoleColors.RED));
                e.printStackTrace();
            }
        } else {
            System.out.println(ConsoleColors.colorize("Invalid choice.", ConsoleColors.RED));
        }
    }
}
