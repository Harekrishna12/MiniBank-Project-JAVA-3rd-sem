package service;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import org.junit.Test;

public class BankServiceTest {

    private BankService newBank() throws Exception {
        Path log = Files.createTempFile("transactions", ".log");
        return new BankService(log);
    }

    @Test
    public void depositIncreasesBalance() throws Exception {
        BankService bank = newBank();
        Account a = bank.openAccount("Riya", 1000);
        bank.deposit(a.getAccountNumber(), 500);
        assertEquals(1500, bank.find(a.getAccountNumber()).getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void withdrawOverLimitThrows() throws Exception {
        BankService bank = newBank();
        Account a = bank.openAccount("Arjun", 1000); // savings keeps a minimum of 1000
        bank.withdraw(a.getAccountNumber(), 500);
    }

    @Test
    public void concurrentDepositsKeepBalanceCorrect() throws Exception {
        BankService bank = newBank();
        Account a = bank.openAccount("Meera", 0);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int t = 0; t < 10; t++) {
            pool.submit(() -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        bank.deposit(a.getAccountNumber(), 1);
                    } catch (BankException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        assertEquals(1000, bank.find(a.getAccountNumber()).getBalance());
    }
}
