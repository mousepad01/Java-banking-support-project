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

        DbInit.init();

        Client client;
        client = Client.newClient("Aa", "Bb", "2000-12-29");

        Employee emp;
        emp = Employee.newEmployee("Ff", "Bgb", "2001-11-27", "str 1", "a@y",
                "0713444555", "1999-10-09", "INFORMATICIAN",
                "REMOTE", 4000);

        DepotAccount depotAccount = client.createDepotAccount("cont de depozit", emp, "ONE MONTH", 1300);
        SavingsAccount savingsAccount = client.createSavingsAccount("cont de economii", emp);

        CurrentAccount cont = client.createCurrentAccount("cont curent", emp);

        cont.add(2700);

        DebitCard cardDebit = client.createDebitCard("cont curent", "card de debit", emp);

        System.out.println(cardDebit.getBalance());
        cardDebit.add(1000);
        cardDebit.extract(200);

        System.out.println(cont.getBalance());

        CreditCard cr = client.createCreditCard("card de credit", 1200, emp);
        CreditCard cr2 = client.createCreditCard("credit pt ceva", 3500, emp);

        int pin = cr.initPin();

        AccountWithCard cont2 = client.createBasicAccount("cont de baza", emp);

        /*System.out.println(client.getClientFunds());
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

            for(Person p : npersons)
                System.out.println(p);

            Client cl = (Client) npersons.get(0);

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

        Logger log = Logger.getLogger();
        log.endLogging();*/
    }
}
