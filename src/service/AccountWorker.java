package service;

import exception.BankException;
import model.Account;

public class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println("Worker started on " + Thread.currentThread().getName());
        try {
            for (int i = 0; i < times; i++) {
                account.deposit(amount);
            }
        } catch (BankException e) {
            System.out.println("Worker error: " + e.getMessage());
        }
        System.out.println("Worker finished on " + Thread.currentThread().getName());
    }
}
