package Rest;

import java.util.Objects;

public class Classtest1 {

    public int a;
    public String b;

    public static int s1;

    static{
        s1 = 987;
        System.out.println("clas1 static");
    }

    public static void sm(){
        System.out.println("sm1");
    }

    @Override
    public String toString() {
        return "Classtest1{" +
                "a=" + a +
                ", b='" + b + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classtest1 that = (Classtest1) o;
        return a == that.a && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
