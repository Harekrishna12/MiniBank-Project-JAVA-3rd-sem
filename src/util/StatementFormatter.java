package util;

import model.Account;

public class StatementFormatter {

    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("==== Account Statement ====\n");
        sb.append("Account No : ").append(account.getAccountNumber()).append('\n');
        sb.append("Holder     : ").append(account.getOwnerName()).append('\n');
        sb.append("Balance    : Rs. ").append(account.getBalance()).append('\n');
        sb.append("Status     : ").append(account.isActive() ? "ACTIVE" : "INACTIVE").append('\n');
        sb.append("===========================");
        return sb.toString();
    }
}
