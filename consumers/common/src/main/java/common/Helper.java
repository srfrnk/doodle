package common;

public class Helper {
    public static boolean objectEquals(Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null)
                || ((obj1 != null && obj2 != null) && obj1.equals(obj2));
    }
}
