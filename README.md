# MiniBank - Console Banking Application (Java)

MiniBank is a console banking application written in Java. It supports savings, current and
fixed deposit accounts, validates input, keeps a transaction log, saves its data between runs,
and processes batches of transactions on a thread pool.

## Requirements
- JDK 17 or later (tested with JDK 21)
- For the JUnit tests: JUnit 4.13.2 and hamcrest-core 1.3 (put them in a `lib` folder)

## Project structure
- `MiniBank.java` - main class and menu
- `model/` - Account (abstract), SavingsAccount, CurrentAccount, FixedDepositAccount, Customer, interfaces
- `util/` - Validator, CommandParser, StatementFormatter, TransactionLog, Stats, AnnotationValidator
- `service/` - BankService, Repository<T>, AccountStore, TransactionProcessor, ReportGenerator, StatePersister
- `exception/` - BankException and its subclasses
- `model/annotation/` - @Id, @Positive, @MaxLength

## Build and run
```
javac -d out $(find src -name "*.java" ! -name "*Test.java")
java -cp out MiniBank
```
Or run the packaged JAR:
```
java -jar minibank.jar
```

## Tests
```
javac -cp "out:lib/*" -d outTest src/service/BankServiceTest.java
java -cp "outTest:out:lib/*" org.junit.runner.JUnitCore service.BankServiceTest
```

## Data files (created by the program)
- `data/accounts.dat` - all accounts, saved on exit (menu option 8) and reloaded on start-up
- `logs/transactions.log` - one line per deposit, withdrawal and transfer
- `reports/summary.txt` - end-of-day summary written by menu option 7

## Menu
1. Open account  2. Deposit  3. Withdraw  4. Transfer  5. Statement  6. List accounts  7. Report  8. Exit
"# MiniBank-Project-JAVA-3rd-sem" 
"# MiniBank-Project-JAVA-3rd-sem" 
