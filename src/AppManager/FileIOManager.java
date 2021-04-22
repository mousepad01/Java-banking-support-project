package AppManager;

/* ma voi folosi de clase de tip Buffered
* pentru a economisi numarul de apeluri catre OS
*
* voi crea metode pentru citit/ afisat cate un element in parte
* pentru mai multe obiecte deodata ma voi folosi de aceste metode spre exemplu in cadrul stream urilor */

/* imi pun problema cum voi reconstrui conturile si cardurile
* ale caror constructori nu permit initializarea cu o suma de bani
* voi creea doua noi clase: UnlinkedAccount, UnlinkedCard
* pe post de obiecte intermediare extrase din db sau din fisiere
* relatia client-cont-card va trebui reconstruita in aceasta ordine */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class FileIOManager {

    private static FileIOManager instance;

    public static FileIOManager getInstance() {

        if (instance == null)
            instance = new FileIOManager();

        return instance;
    }
    public static boolean isInitialized() {
        return instance != null;
    }

    //-----

    public void saveInFilePersons(Iterable <Person> toWriteCollection, String fileName) throws IOException{

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            for (Person toWrite : toWriteCollection)
                saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Person toWrite, String fileName) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Person toWrite, BufferedWriter writer) throws IOException{

        writer.write(toWrite.getName() + ";");
        writer.write(toWrite.getSurname() + ";");
        writer.write(toWrite.getId() + ";");
        writer.write(toWrite.getBirthDate() + ";");
        writer.write(toWrite.getAddress() + ";");
        writer.write(toWrite.getEmail() + ";");
        writer.write(toWrite.getPhoneNumber() + ";");

        if(toWrite instanceof Employee){
            Employee toWriteEmp = (Employee) toWrite;

            writer.write("EMPLOYEE: ");
            writer.write(toWriteEmp.getHireDate() + ";");
            writer.write(toWriteEmp.getJob() + ";");
            writer.write(toWriteEmp.getWorkplace() + ";");
            writer.write(toWriteEmp.getSalary() + ";");
        }

        if(toWrite instanceof Client){
            Client toWriteClient = (Client) toWrite;

            writer.write("CLIENT: ");
            writer.write(toWriteClient.getRegistrationDate() + ";");

            StringBuilder accounts = new StringBuilder("accounts: ");

            Iterator<HashMap.Entry<String, Account>> ita = toWriteClient.getAllAccounts();

            while(ita.hasNext()){

                HashMap.Entry<String, Account> strAcc = ita.next();
                accounts.append(strAcc.getValue().getAccountId());
                accounts.append(",");
            }

            writer.write(accounts.toString() + ";");

            StringBuilder cards = new StringBuilder("cards: ");

            Iterator<HashMap.Entry<String, Card>> itc = toWriteClient.getAllCards();

            while(itc.hasNext()){

                HashMap.Entry<String, Card> strAcc = itc.next();
                cards.append(strAcc.getValue().getCardId());
                cards.append(",");
            }

            writer.write(cards.toString() + ";");
        }
    }

    //-----

    public void saveInFileAccounts(Iterable <Account> toWriteCollection, String fileName) throws IOException{

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            for (Account toWrite : toWriteCollection)
                saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Account toWrite, String fileName) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Account toWrite, BufferedWriter writer) throws IOException {

        writer.write(toWrite.getAccountId() + ";");
        writer.write(toWrite.getOwner().getId() + ";");
        writer.write(toWrite.getName() + ";");
        writer.write(toWrite.getContractAssistant().getId() + ";");
        writer.write(toWrite.getCreationDate().toString() + ";");
        writer.write(toWrite.getBalance() + ";");
        writer.write(toWrite.getFlags() + ";");

        if(toWrite instanceof AccountWithCard){
            AccountWithCard toWriteAC = (AccountWithCard) toWrite;

            writer.write("ACCOUNT WITH CARD: ");
            writer.write(toWriteAC.getAssociatedCard().getCardId() + ";");

            if(toWriteAC instanceof BasicAccount)
                writer.write("BASIC ACCOUNT: ;");

            if(toWriteAC instanceof CurrentAccount){
                CurrentAccount toWriteCA = (CurrentAccount) toWriteAC;

                writer.write("CURRENT ACCOUNT: ");
                writer.write(toWriteCA.getTransactionFee() + ";");
                writer.write(toWriteCA.getExtractFee() + ";");
                writer.write(toWriteCA.getAddFee() + ";");
            }
        }

        if(toWrite instanceof SavingsAccount){
            SavingsAccount toWriteSA = (SavingsAccount) toWrite;

            writer.write("SAVINGS ACCOUNT: ");
            writer.write(toWriteSA.getInterestRate() + ";");
            writer.write(toWriteSA.getLastUpdated().toString() + ";");
        }

        if(toWrite instanceof DepotAccount){
            DepotAccount toWriteDA = (DepotAccount) toWrite;

            writer.write("DEPOT ACCOUNT: ");
            writer.write(toWriteDA.getTerm() + ";");
            writer.write(toWriteDA.getLastUpdatedTerm().toString() + ";");
        }
    }

    //-----

    public void saveInFile(Card toWrite, String fileName){}
}
