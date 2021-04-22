package AppManager;

import java.sql.Date;

/* motiv pentru care am tinut clasa cu metode statice
* sunt o colectie de metode "globale" care au sens sa fie la fel
* si nu ar avea sens sa imi pun problema polimorfismului
* de asemenea, nu contin campuri cu date sau stari interne
* deci instantierea nu ar avea sens
* (fac o analogie cu bibliotecti pt functii matematice) */

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

    public static boolean personIdOk(String toCheck){
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
