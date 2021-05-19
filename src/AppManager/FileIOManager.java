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

import AppIO.UnlinkedAccount;
import AppIO.UnlinkedCard;

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

    public String checkNull(String toCheck){
        return toCheck.equals("null") ? null : toCheck;
    }

    //-----------

    public void saveInFilePersons(Iterable <Person> toWriteCollection, String fileName) throws IOException{

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){

            for (Person toWrite : toWriteCollection){

                saveInFile(toWrite, writer);
                writer.write('\n');
            }

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

            name = checkNull(info[0].substring(8));
            surname = checkNull(info[1]);
            id = checkNull(info[2]);
            birthDateStr = checkNull(info[3]);
            address = checkNull(info[4]);
            email = checkNull(info[5]);
            phoneNumber = checkNull(info[6]);

            if(info[7].length() > 8 && info[7].startsWith("CLIENT: ")){

                String registrationDateStr;
                ArrayList<String> accountsIds;
                ArrayList<String> cardsIds;

                registrationDateStr = checkNull(info[7].substring(8));

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

                hireDateStr = checkNull(info[7].substring(10));
                job = checkNull(info[8]);
                workplace = checkNull(info[9]);
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

            for (Account toWrite : toWriteCollection){

                saveInFile(toWrite, writer);
                writer.write('\n');
            }

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

            accountId = checkNull(info[0].substring(9));
            ownerId = checkNull(info[1]);
            name = checkNull(info[2]);
            contractAssistantId = checkNull(info[3]);
            creationDate = checkNull(info[4]) == null ? null : Date.valueOf(info[4]);
            balance = Double.parseDouble(info[5]);
            flags = Byte.parseByte(info[6], 10);

            if(info[7].length() > 19 && info[7].startsWith("ACCOUNT WITH CARD: ")){

                associatedCardId = checkNull(info[7].substring(19));

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
                lastUpdated = checkNull(info[8]) == null ? null : Date.valueOf(info[8]);
            }

            else if (info[7].length() > 15 && info[7].startsWith("DEPOT ACCOUNT: ")){

                type = "DEPOT";

                term = Integer.parseInt(info[7].substring(15));
                lastUpdatedTerm = checkNull(info[8]) == null ? null : Date.valueOf(info[8]);
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

            for (Card toWrite : toWriteCollection){

                saveInFile(toWrite, writer);
                writer.write('\n');
            }

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

            boolean activeStatus = false;
            double creditTotalAmmount = 0, creditAmmount = 0;

            String associatedAccountId = null;

            if(!(info[0].length() > 6) || !(info[0].startsWith("CARD: ")))
                throw new InvalidPropertiesFormatException("bad file format");

            suspendedStatus = Boolean.getBoolean(info[0].substring(6));
            pinIsInitialized = Boolean.getBoolean(info[1]);

            if(info[2].equals("null"))
                pinHash = null;

            else{

                info[2] = info[2].substring(1, info[2].length() - 1); // elimin "[" si "]"

                String[] pinHashStr = info[2].split(", ");
                pinHash = new byte[pinHashStr.length];

                for(int i = 0; i < pinHashStr.length; i++)
                    pinHash[i] = Byte.parseByte(pinHashStr[i], 10);
            }

            contractAssistantId = checkNull(info[3]);
            ownerId = checkNull(info[4]);
            cardId = checkNull(info[5]);
            name = checkNull(info[6]);
            emissionDate = checkNull(info[7]) == null ? null : Date.valueOf(info[7]);

            if(info[8].length() > 12 && info[8].startsWith("DEBIT CARD: ")){

                type = "DEBIT";

                associatedAccountId = checkNull(info[8].substring(12));
            }

            else if(info[8].length() > 13 && info[8].startsWith("CREDIT CARD: ")){

                type = "CREDIT";

                activeStatus = Boolean.getBoolean(info[8].substring(13));
                creditTotalAmmount = Double.parseDouble(info[9]);
                creditAmmount = Double.parseDouble(info[10]);
            }

            else
                throw new InvalidPropertiesFormatException("bad file format");

            toReturn.add(new UnlinkedCard(type, suspendedStatus, pinIsInitialized, pinHash, contractAssistantId,
                                            ownerId, cardId, name, emissionDate, activeStatus, creditTotalAmmount,
                                            creditAmmount, associatedAccountId));

            contents = reader.readLine();
        }

        return toReturn;
    }

    //------------

    // (singura) metoda care recreaza obiectele in memorie care sunt scoase din fisiere
    // nu voi putea crea in memorie carduri sau conturi separate de clienti
    // complexitate timp (considerand query urile pe hashmap O(1)): O(|persons| + |accounts| + |cards|)
    public ArrayList<Person> link(ArrayList<Person> persons, ArrayList<UnlinkedAccount> accounts,
                                  ArrayList<UnlinkedCard> cards) throws InstantiationException {

        // preprocesari pentru query uri mai eficiente
        // in cea mai mare parte creez map uri

        HashMap<String, ArrayList<UnlinkedAccount>> ownerToAccount = new HashMap<>(); // [(ownerId, [(accId, acc)])]
        HashMap<String, ArrayList<UnlinkedCard>> ownerToCard = new HashMap<>();       // [(ownerId, [(cardId, card)])]
        HashMap<String, Employee> employeeMap = new HashMap<>();                            // [(empId, emp)]

        for(UnlinkedAccount a : accounts){

            if(!ownerToAccount.containsKey(a.ownerId()))
                ownerToAccount.put(a.ownerId(), new ArrayList<>());

            ownerToAccount.get(a.ownerId()).add(a);
        }

        for(UnlinkedCard c : cards){

            if(!ownerToCard.containsKey(c.ownerId()))
                ownerToCard.put(c.ownerId(), new ArrayList<>());

            ownerToCard.get(c.ownerId()).add(c);
        }

        for(Person p : persons){

            if(p instanceof Employee)
                employeeMap.put(p.getId(), (Employee) p);
        }

        for(Person p : persons){

            if(p instanceof Employee)
                continue;

            Client client = (Client) p;

            ArrayList<UnlinkedAccount> toLinkAccounts = ownerToAccount.get(client.getId());
            ArrayList<UnlinkedCard> toLinkCards = ownerToCard.get(client.getId());

            HashMap<String, Account> linkedAccounts = new HashMap<>();

            for (UnlinkedAccount ua : toLinkAccounts) {

                Employee contractAssistant = employeeMap.get(ua.contractAssistantId());

                switch (ua.type()) {

                    case "BASIC" -> {
                        BasicAccount ba = new BasicAccount(ua.accountId(), client, ua.name(), contractAssistant,
                                ua.creationDate(), ua.balance(), ua.flags(), null);
                        client.linkAccount(ba);

                        linkedAccounts.put(ba.getAccountId(), ba);
                    }

                    case "CURRENT" -> {
                        CurrentAccount ca = new CurrentAccount(ua.accountId(), client, ua.name(), contractAssistant,
                                ua.creationDate(), ua.balance(), ua.flags(), null,
                                ua.transactionFee(), ua.extractFee(), ua.addFee());
                        client.linkAccount(ca);

                        linkedAccounts.put(ca.getAccountId(), ca);
                    }

                    case "SAVINGS" -> {
                        SavingsAccount sa = new SavingsAccount(ua.accountId(), client, ua.name(), contractAssistant,
                                ua.creationDate(), ua.balance(), ua.flags(),
                                ua.interestRate(), ua.lastUpdated());
                        client.linkAccount(sa);
                    }

                    case "DEPOT" -> {
                        DepotAccount da = new DepotAccount(ua.accountId(), client, ua.name(), contractAssistant,
                                ua.creationDate(), ua.balance(), ua.flags(),
                                ua.term(), ua.lastUpdatedTerm());
                        client.linkAccount(da);
                    }

                    default -> throw new InstantiationException("invalid account type");
                }
            }

            for(UnlinkedCard uc : toLinkCards){

                Employee contractAssistant = employeeMap.get(uc.contractAssistantId());

                switch (uc.type()){

                    case "DEBIT" -> {
                        DebitCard dc = new DebitCard(uc.suspendedStatus(), uc.pinIsInitialized(), uc.pinHash(),
                                            contractAssistant, client, uc.cardId(), uc.name(), uc.emissionDate(),
                                            (AccountWithCard) linkedAccounts.get(uc.associatedAccountId()));
                        client.linkCard(dc);
                    }

                    case "CREDIT" -> {

                        CreditCard cc = new CreditCard(uc.suspendedStatus(), uc.pinIsInitialized(), uc.pinHash(),
                                                contractAssistant, client, uc.cardId(), uc.name(), uc.emissionDate(),
                                                uc.activeStatus(), uc.creditTotalAmmount(), uc.creditAmmount());
                        client.linkCard(cc);
                    }
                }
            }
        }

        return persons;
    }
}
