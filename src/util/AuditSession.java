package util;

public class AuditSession implements AutoCloseable {
    private final String name;

    public AuditSession(String name) {
        this.name = name;
        System.out.println("Session opened: " + name);
    }

    @Override
    public void close() {
        System.out.println("Session closed: " + name);
    }
}
