package App;
import AppManager.*;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        client = new Client("a", "b", "2015-12-29");

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

        CreditCard cr = client.createCreditCard("card de credit", 1200, emp, "cr1");
        CreditCard cr2 = client.createCreditCard("credit pt ceva", 3500, emp, "cr2");

        AccountWithCard cont2 = client.createBasicAccount("cont de baza", emp, "ac2");
        //AccountWithCard cont2 = (AccountWithCard) client.getAccount("cont de baza");

        System.out.println(client.getClientFunds());
        System.out.println(client.getCreditDebt());

        FileIOManager fileIOManager = FileIOManager.getInstance();

        cardDebit.initPin();

        System.out.println(client);
        System.out.println(emp);
        System.out.println(cont); // cont curent
        System.out.println(cont2); // cont de baza
        System.out.println(cardDebit); // card de debit - cont curent
        System.out.println(cr); // card de credit
        System.out.println(cr2); // credit pt ceva

        try{

            ArrayList<Person> persons = new ArrayList<>();
            persons.add(client);
            persons.add(emp);

            fileIOManager.saveInFilePersons(persons, "persons.txt");

            ArrayList<Account> accs = new ArrayList<>();
            accs.add(cont);
            accs.add(cont2);

            fileIOManager.saveInFileAccounts(accs, "accounts.txt");

            ArrayList<Card> cards = new ArrayList<>();
            cards.add(cardDebit);
            cards.add(cr);
            cards.add(cr2);

            fileIOManager.saveInFileCards(cards, "cards.txt");

            System.out.println("");

            ArrayList<Person> rpersons = fileIOManager.readFromFilePersons("persons.txt");
            ArrayList<UnlinkedAccount> raccs = fileIOManager.readFromFileAccounts("accounts.txt");
            ArrayList<UnlinkedCard> rcards = fileIOManager.readFromFileCards("cards.txt");

            ArrayList<Person> npersons = fileIOManager.link(rpersons, raccs, rcards);

            for(Person p : persons)
                System.out.println(p);

            Client cl = (Client) persons.get(0);

            Iterator<HashMap.Entry<String, Account>> ita = cl.getAllAccounts();
            Iterator<HashMap.Entry<String, Card>> itc = cl.getAllCards();

            while(ita.hasNext()){

                Account a = ita.next().getValue();
                System.out.println(a);
                System.out.println("-----");
            }

            while(itc.hasNext()){

                Card c = itc.next().getValue();
                System.out.println(c);
                System.out.println("-----");
            }

        }
        catch(IOException | InstantiationException err){
            System.out.println(err);
        }
    }
}
