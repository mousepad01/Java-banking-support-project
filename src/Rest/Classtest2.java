package Rest;

import java.util.Objects;

public class Classtest2 extends Classtest1{

    public String c;
    public long d;

    public static int s2;

    public static void sm(){
        System.out.println("sm2");
    }

    static{
        s2 = 3456;
        System.out.println("class2 static");
    }

    @Override
    public String toString() {
        return "Classtest2{" +
                "a=" + a +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d=" + d +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Classtest2 that = (Classtest2) o;
        return d == that.d && Objects.equals(c, that.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), c, d);
    }
}
