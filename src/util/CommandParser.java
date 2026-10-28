package util;

public class CommandParser {

    public static Command parse(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected: TYPE ACCOUNT AMOUNT, got: " + line);
        }
        TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
        long amount = Long.parseLong(parts[2]);
        return new Command(type, parts[1], amount);
    }
}
