package util;

public class Stats {

    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("cannot average an empty array");
        }
        double sum = 0;
        for (N v : values) {
            sum += v.doubleValue();
        }
        return sum / values.length;
    }
}
