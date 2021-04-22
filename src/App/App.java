package App;
import AppManager.*;

//import java.time.*;

public class App {

    /// PENTRU DEBUG
    public static void dormi(long milisec){

        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Client client;
        client = new Client("a", "b", "2015-12-29", "idclient002");

        Employee emp;
        emp = new Employee("a", "b", "2015-12-29", "str 1", "a@y",
                "0713444555", "2018-10-09", "INFORMATICIAN",
                "REMOTE", "id01", 4000);

        CurrentAccount cont = client.createCurrentAccount("cont curent", emp, "cont1");
        //CurrentAccount cont = (CurrentAccount) client.getAccount("cont curent");

        cont.add(2700);

        DebitCard cardDebit = client.createDebitCard("cont curent", "card de debit", emp, "cardD");
        //DebitCard cardDebit = (DebitCard) client.getCard("card de debit");

        System.out.println(cardDebit.getBalance());
        cardDebit.add(1000);
        cardDebit.extract(200);

        System.out.println(cont.getBalance());

        client.createCreditCard("card de credit", 1200, emp, "cr1");
        client.createCreditCard("credit pt ceva", 3500, emp, "cr2");

        AccountWithCard cont2 = client.createBasicAccount("cont de baza", emp, "ac2");
        //AccountWithCard cont2 = (AccountWithCard) client.getAccount("cont de baza");

        System.out.println(client.getClientFunds());
        System.out.println(client.getCreditDebt());


    }
}
