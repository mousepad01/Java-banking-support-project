package App;

import Rest.*;

import java.util.Date;
import java.util.Scanner;
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
        client = new Client("a", "b", "2015-12-29", "1234", "2021-03-18");

        Employee emp;
        emp = new Employee("a", "b", "2015-12-29", "str 1", "1234", "a@y",
                "0713444555", "2018-10-09", "INFORMATICIAN",
                "REMOTE", 4000);

        client.createCurrentAccount("cont curent", emp);
        CurrentAccount cont = (CurrentAccount) client.getAccount("cont curent");

        cont.add(2700);

        client.createDebitCard("cont curent", "card de debit", emp);
        DebitCard cardDebit = (DebitCard) client.getCard("card de debit");

        System.out.println(cardDebit.getBalance());
        cardDebit.add(1000);
        cardDebit.extract(200);

        System.out.println(cardDebit.getAccountName());

        System.out.println(cont.getBalance());
    }
}
