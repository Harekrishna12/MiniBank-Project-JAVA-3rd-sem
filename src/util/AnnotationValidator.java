package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.MaxLength;
import model.annotation.Positive;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        // walk the class and its superclasses so inherited fields are checked too
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (field.isAnnotationPresent(Positive.class)
                            && value instanceof Number n && n.longValue() <= 0) {
                        errors.add(field.getName() + " " + field.getAnnotation(Positive.class).message());
                    }
                    if (field.isAnnotationPresent(MaxLength.class) && value instanceof String s) {
                        int max = field.getAnnotation(MaxLength.class).value();
                        if (s.length() > max) {
                            errors.add(field.getName() + " length must be <= " + max);
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add(field.getName() + " could not be read");
                }
            }
        }
        return errors.toArray(new String[0]);
    }
}
