package service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import model.Account;

public class StatePersister {

    public static void save(Account[] accounts, Path file) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(accounts);
        }
    }

    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        Account[] accounts;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            accounts = (Account[]) in.readObject();
        }
        int highest = 0;
        for (Account a : accounts) {
            highest = Math.max(highest, Integer.parseInt(a.getAccountNumber().substring(2)));
        }
        Account.syncCounterAtLeast(highest);
        return accounts;
    }
}
