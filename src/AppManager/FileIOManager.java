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
* relatia client-cont-card va trebui reconstruita in aceasta ordine:
* client, angajat responsabil cu acele contracte, conturile persoanei, cardurile persoanei */

import java.io.*;
import java.sql.Date;
import java.util.*;

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

    //-----------

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

    public void saveInFile(Person toWrite, BufferedWriter writer) throws IOException {

        writer.write(toWrite.getSerialization());
    }

    public ArrayList<Person> readFromFilePersons(String fileName) throws IOException {

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            return readFromFilePersons(reader);
        }
    }

    public ArrayList<Person> readFromFilePersons(BufferedReader reader) throws IOException {

        ArrayList<Person> toReturn = new ArrayList<>();

        String contents = reader.readLine();

        while(contents != null){

            String[] info = contents.split(";");

            String name, surname, id, address, email, phoneNumber, birthDateStr;

            if(!(info[0].length() > 8) || !info[0].startsWith("PERSON: "))
                throw new InvalidPropertiesFormatException("bad file format");

            name = info[0].substring(8);
            surname = info[1];
            id = info[2];
            birthDateStr = info[3];
            address = info[4];
            email = info[5];
            phoneNumber = info[6];

            if(info[7].length() > 8 && info[7].startsWith("CLIENT: ")){

                String registrationDateStr;
                ArrayList<String> accountsIds;
                ArrayList<String> cardsIds;

                registrationDateStr = info[7].substring(8);

                if(!(info[8].length() > 10) || !(info[8].startsWith("accounts: ")))
                    throw new InvalidPropertiesFormatException("bad file format");

                if(!(info[9].length() > 7) || !(info[9].startsWith("cards: ")))
                    throw new InvalidPropertiesFormatException("bad file format");

                accountsIds = new ArrayList<>(Arrays.asList(info[8].split(",")));
                cardsIds = new ArrayList<>(Arrays.asList(info[9].split(",")));

                toReturn.add(new Client(name, surname, birthDateStr, address, email, phoneNumber,
                                        registrationDateStr, id, accountsIds, cardsIds));
            }

            else if(info[7].length() > 10 && info[7].startsWith("EMPLOYEE: ")){

                String hireDateStr;
                String job, workplace;
                int salary;

                hireDateStr = info[7].substring(10);
                job = info[8];
                workplace = info[9];
                salary = Integer.parseInt(info[10]);

                toReturn.add(new Employee(name, surname, birthDateStr, address, email, phoneNumber,
                                            hireDateStr, job, workplace, id, salary));
            }

            else
                throw new InvalidPropertiesFormatException("bad file format");

            contents = reader.readLine();
        }

        return toReturn;
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

        writer.write(toWrite.getSerialization());
    }

    public ArrayList<UnlinkedAccount> readFromFileAccounts(String fileName) throws IOException {

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            return readFromFileAccounts(reader);
        }
    }

    public ArrayList<UnlinkedAccount> readFromFileAccounts(BufferedReader reader) throws IOException {

        ArrayList<UnlinkedAccount> toReturn = new ArrayList<>();

        String contents = reader.readLine();

        while(contents != null){

            String[] info = contents.split(";");

            String type;
            String accountId, ownerId, name, contractAssistantId;
            Date creationDate;
            double balance;
            byte flags;

            String associatedCardId = null;

            double transactionFee = 0, extractFee = 0, addFee = 0;

            double interestRate = 0;
            Date lastUpdated = null;

            int term = 0;
            Date lastUpdatedTerm = null;

            if(!(info[0].length() > 9) || !(info[0].startsWith("ACCOUNT: ")))
                throw new InvalidPropertiesFormatException("bad file format");

            accountId = info[0].substring(9);
            ownerId = info[1];
            name = info[2];
            contractAssistantId = info[3];
            creationDate = Date.valueOf(info[4]);
            balance = Double.parseDouble(info[5]);
            flags = Byte.parseByte(info[6], 10);

            if(info[7].length() > 19 && info[7].startsWith("ACCOUNT WITH CARD: ")){

                associatedCardId = info[7].substring(19);

                if(info[8].length() > 17 && info[8].startsWith("CURRENT ACCOUNT: ")){

                    type = "CURRENT";

                    transactionFee = Double.parseDouble(info[8].substring(17));
                    extractFee = Double.parseDouble(info[9]);
                    addFee = Double.parseDouble(info[10]);
                }

                else if (info[8].length() >= 15 && info[8].startsWith("BASIC ACCOUNT: ")){

                    type = "BASIC";
                }

                else
                    throw new InvalidPropertiesFormatException("bad file format");
            }

            else if (info[7].length() > 17 && info[7].startsWith("SAVINGS ACCOUNT: ")){

                type = "SAVINGS";

                interestRate = Double.parseDouble(info[7].substring(17));
                lastUpdated = Date.valueOf(info[8]);
            }

            else if (info[7].length() > 15 && info[7].startsWith("DEPOT ACCOUNT: ")){

                type = "DEPOT";

                term = Integer.parseInt(info[7].substring(15));
                lastUpdatedTerm = Date.valueOf(info[8]);
            }

            else
                throw new InvalidPropertiesFormatException("bad file format");

            toReturn.add(new UnlinkedAccount(type, accountId, ownerId, name, contractAssistantId, creationDate,
                                                balance, flags, associatedCardId, transactionFee, extractFee,
                                                addFee, interestRate, lastUpdated, term, lastUpdatedTerm));

            contents = reader.readLine();
        }

        return toReturn;
    }

    //-----

    public void saveInFileCards(Iterable <Card> toWriteCollection, String fileName) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            for (Card toWrite : toWriteCollection)
                saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Card toWrite, String fileName) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            saveInFile(toWrite, writer);
        }
    }

    public void saveInFile(Card toWrite, BufferedWriter writer) throws IOException {

        writer.write(toWrite.getSerialization());
    }

    public ArrayList<UnlinkedCard> readFromFileCards(String fileName) throws IOException {

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            return readFromFileCards(reader);
        }
    }

    public ArrayList<UnlinkedCard> readFromFileCards(BufferedReader reader) throws IOException {

        ArrayList<UnlinkedCard> toReturn = new ArrayList<>();

        String contents = reader.readLine();

        while(contents != null){

            String[] info = contents.split(";");

            String type;
            boolean suspendedStatus, pinIsInitialized;
            byte[] pinHash;
            String contractAssistantId, ownerId, cardId, name;
            Date emissionDate;

            boolean activeStatus;
            double creditTotalAmmount, creditAmmount;

            Account associatedAccount;

            if(!(info[0].length() > 6) || !(info[0].startsWith("CARD: ")))
                throw new InvalidPropertiesFormatException("bad file format");



            contents = reader.readLine();
        }

        return toReturn;
    }

    //------------
}
