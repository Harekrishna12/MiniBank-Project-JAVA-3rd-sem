package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile) throws IOException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        List<Path> logs = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder)) {
            for (Path p : stream) {
                if (Files.isRegularFile(p) && p.getFileName().toString().endsWith(".log")
                        && Files.size(p) > 0) {
                    logs.add(p);
                }
            }
        }
        Collections.sort(logs);

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        List<String> lines = new ArrayList<>();
        lines.add("MiniBank end-of-day summary");
        lines.add(String.format("%-20s %10s  %s", "File", "Size(bytes)", "Last modified"));
        for (Path log : logs) {
            BasicFileAttributes attrs = Files.readAttributes(log, BasicFileAttributes.class);
            lines.add(String.format("%-20s %10d  %s", log.getFileName(), attrs.size(),
                    fmt.format(attrs.lastModifiedTime().toInstant())));
            try (BufferedReader reader = Files.newBufferedReader(log)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length != 3) continue;
                    long amount = Long.parseLong(parts[2]);
                    if (parts[0].equals("DEPOSIT")) totalDeposits += amount;
                    if (parts[0].equals("WITHDRAW")) totalWithdrawals += amount;
                }
            }
        }
        lines.add("Total deposits: " + totalDeposits);
        lines.add("Total withdrawals: " + totalWithdrawals);
        lines.add("Net change: " + (totalDeposits - totalWithdrawals));
        if (reportFile.getParent() != null) {
            Files.createDirectories(reportFile.getParent());
        }
        Files.write(reportFile, lines);
    }
}
