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

        client.createDepotAccount("cont de depozit", emp, "ONE MONTH", 10000);
        client.createCurrentAccount("contul curent", emp);
        Account contul1a = client.getAccount("cont de depozit");
        Account contul2a = client.getAccount("contul curent");

        DepotAccount contul1 = (DepotAccount) contul1a;
        //CurrentAccount contul2 = (CurrentAccount) contul2a;
        /*contul1.add(1);
        dormi(1300);
        System.out.println(contul1.getBalance());

        contul1.suspendAccount();
        //contul1.dropSuspendedInterest();

        dormi(1400);
        System.out.println(contul1.getBalance());

        contul1.dropSuspended();

        dormi(1410);

        System.out.println(contul1.getBalance());*/

    }
}
