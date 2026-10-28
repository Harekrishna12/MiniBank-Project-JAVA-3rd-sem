package model;

import java.io.Serializable;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;
    private static int accountCounter = 0;

    @Id
    private final String accountNumber;
    @MaxLength(30)
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;

    public Account(String ownerName, long openingBalance) {
        accountCounter++;
        this.accountNumber = String.format("AC%04d", accountCounter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    // called after loading accounts from a file so new numbers do not repeat old ones
    public static synchronized void syncCounterAtLeast(int n) {
        if (accountCounter < n) {
            accountCounter = n;
        }
    }

    // Used in tests to create a second object that carries an existing account number
    Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    // how much could be withdrawn right now, according to this account type's rules
    public abstract long availableBalance();

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive: " + amount);
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive: " + amount);
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(amount - availableBalance());
        }
        balance -= amount;
        return true;
    }

    // Moves money to another account; re-throws any BankException after logging
    public void transfer(Account to, long amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive: " + amount);
        }
        try {
            withdraw(amount);
            to.deposit(amount);
            System.out.println("Transferred " + amount + " from " + accountNumber + " to " + to.getAccountNumber());
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            throw e;
        } finally {
            System.out.println("Transfer attempt finished for " + accountNumber);
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s | balance: %d", accountNumber, ownerName, balance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    @Override
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}
