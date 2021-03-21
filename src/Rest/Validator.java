package Rest;

import java.sql.Date;

public class Validator {

    public static boolean nameOk(String toCheck){
        return toCheck.matches("[A-Z][a-z]+");
    }

    public static boolean emailOk(String toCheck){
        return toCheck.matches("^(.+)@(.+)$");
    }

    public static boolean phoneNumberOk(String toCheck){
        return toCheck.matches("(0)[1-9][0-9]{8}");
    }

    public static boolean ageOk(int toCheck){
        return toCheck >= 18;
    }

    public static boolean ageOk(Date toCheck){

        Date currentDate = new Date(System.currentTimeMillis());
        long milisecAge = currentDate.getTime() - toCheck.getTime();

        return ageOk((int)(milisecAge / ((long)365 * 24 * 60 * 60 * 1000)));
    }

    public static boolean idOk(String toCheck){
        return toCheck.matches("[0-9]{12}");
    }

    public static boolean pastDateOk(Date toCheck){

        Date currentDate = new Date(System.currentTimeMillis());
        long checkDateMilisec = currentDate.getTime() - toCheck.getTime();
        return checkDateMilisec > 0;
    }

    public static boolean pastDateOk(String toCheck){

        return pastDateOk(Date.valueOf(toCheck));
    }

    public static boolean futureDateOk(Date toCheck){

        Date currentDate = new Date(System.currentTimeMillis());
        long checkDateMilisec = currentDate.getTime() - toCheck.getTime();
        return checkDateMilisec < 0;
    }

    public static boolean futureDateOk(String toCheck){

        return futureDateOk(Date.valueOf(toCheck));
    }
}
