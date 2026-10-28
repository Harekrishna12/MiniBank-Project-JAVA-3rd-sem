package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.SavingsAccount;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Connects the repository, validation, exceptions and the transaction log
public class BankService {
    private final Repository<Account> accounts = new Repository<>();
    private final Path logFile;

    public BankService(Path logFile) {
        this.logFile = logFile;
    }

    public synchronized Account openAccount(String ownerName, long openingBalance) throws BankException {
        if (ownerName == null || ownerName.isBlank()) {
            throw new BankException("Owner name must not be blank");
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException("Opening balance must not be negative: " + openingBalance);
        }
        Account account = new SavingsAccount(ownerName.trim(), openingBalance, 1000);
        accounts.save(account);
        return account;
    }

    public synchronized void deposit(String accountNumber, long amount) throws BankException {
        find(accountNumber).deposit(amount);
        log("DEPOSIT " + accountNumber + " " + amount);
    }

    public synchronized void withdraw(String accountNumber, long amount) throws BankException {
        find(accountNumber).withdraw(amount);
        log("WITHDRAW " + accountNumber + " " + amount);
    }

    public synchronized void transfer(String from, String to, long amount) throws BankException {
        find(from).transfer(find(to), amount);
        log("TRANSFER " + from + " " + to + " " + amount);
    }

    public synchronized String statement(String accountNumber) throws BankException {
        return StatementFormatter.buildStatement(find(accountNumber));
    }

    public synchronized List<Account> listAccounts() {
        ArrayList<Account> all = accounts.findAll();
        Collections.sort(all);
        return all;
    }

    public synchronized void loadAccounts(Account[] loaded) {
        for (Account a : loaded) {
            accounts.save(a);
        }
    }

    public synchronized Account[] snapshot() {
        return listAccounts().toArray(new Account[0]);
    }

    public Account find(String accountNumber) throws BankException {
        if (!Validator.isValidAccountNumber(accountNumber)) {
            throw new BankException("Invalid account number: " + accountNumber);
        }
        Account account = accounts.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    private void log(String line) throws BankException {
        try {
            TransactionLog.append(logFile, line);
        } catch (IOException e) {
            throw new BankException("Could not write log: " + e.getMessage());
        }
    }
}
