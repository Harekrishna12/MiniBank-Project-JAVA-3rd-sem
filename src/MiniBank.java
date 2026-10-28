import exception.BankException;
import model.Account;
import service.BankService;
import service.ReportGenerator;
import service.StatePersister;
import service.TransactionProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MiniBank {

    record BankInfo(String name, String branch) {}

    enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST, REPORT, EXIT }

    static final Path DATA_FILE = Path.of("data", "accounts.dat");
    static final Path LOG_FILE = Path.of("logs", "transactions.log");
    static final Path REPORT_FILE = Path.of("reports", "summary.txt");

    public static void main(String[] args) throws Exception {
        BankInfo info = new BankInfo("MiniBank", "Downtown Branch");
        System.out.println(info);

        BankService bank = new BankService(LOG_FILE);

        // Start-up: load saved accounts (Practical 11)
        if (Files.exists(DATA_FILE)) {
            Account[] saved = StatePersister.load(DATA_FILE);
            bank.loadAccounts(saved);
            System.out.println("Loaded " + saved.length + " accounts from " + DATA_FILE);
        } else {
            // First run: open accounts and process a batch on the thread pool (Practical 10 and 12)
            bank.openAccount("Riya", 5000);
            bank.openAccount("Arjun", 2000);
            TransactionProcessor processor = new TransactionProcessor();
            for (int i = 0; i < 4; i++) {
                processor.submit(() -> attempt(() -> bank.deposit("AC0001", 100)));
                processor.submit(() -> attempt(() -> bank.withdraw("AC0001", 50)));
            }
            processor.stop();
            System.out.println("Batch finished, AC0001 statement:");
            System.out.println(bank.statement("AC0001"));
        }

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println();
            for (MenuOption op : MenuOption.values()) {
                System.out.println((op.ordinal() + 1) + ". " + op);
            }
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();
            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Owner name: ");
                        String name = sc.nextLine();
                        System.out.print("Opening balance: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        Account a = bank.openAccount(name, amount);
                        System.out.println("Opened " + a);
                    }
                    case 2 -> {
                        System.out.print("Account number: ");
                        String no = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.deposit(no, amount);
                        System.out.println("Deposited " + amount + " to " + no);
                    }
                    case 3 -> {
                        System.out.print("Account number: ");
                        String no = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.withdraw(no, amount);
                        System.out.println("Withdrew " + amount + " from " + no);
                    }
                    case 4 -> {
                        System.out.print("From account: ");
                        String from = sc.nextLine().trim();
                        System.out.print("To account: ");
                        String to = sc.nextLine().trim();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.transfer(from, to, amount);
                        System.out.println("Transfer complete");
                    }
                    case 5 -> {
                        System.out.print("Account number: ");
                        System.out.println(bank.statement(sc.nextLine().trim()));
                    }
                    case 6 -> {
                        List<Account> all = bank.listAccounts();
                        System.out.println("Accounts (" + all.size() + "):");
                        for (Account a : all) {
                            System.out.println("  " + a);
                        }
                    }
                    case 7 -> {
                        ReportGenerator.generate(Path.of("logs"), REPORT_FILE);
                        Files.readAllLines(REPORT_FILE).forEach(System.out::println);
                    }
                    case 8 -> {
                        running = false;
                        StatePersister.save(bank.snapshot(), DATA_FILE);
                        System.out.println("Accounts saved to " + DATA_FILE);
                    }
                    default -> System.out.println("Invalid choice, please try again");
                }
            } catch (BankException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Thank you for banking with " + info.name() + ". Goodbye!");
        sc.close();
    }

    interface Action {
        void run() throws BankException;
    }

    static void attempt(Action action) {
        try {
            action.run();
        } catch (BankException e) {
            System.out.println(Thread.currentThread().getName() + " failed: " + e.getMessage());
        }
    }
}
