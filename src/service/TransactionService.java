package service;

import exception.BankException;
import model.Account;

public class TransactionService {

    public static boolean withdrawWithRule(Account account, long amount, WithdrawRule rule) throws BankException {
        if (!rule.allow(account, amount)) {
            return false;
        }
        return account.withdraw(amount);
    }
}
