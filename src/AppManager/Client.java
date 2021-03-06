package AppManager;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class Client extends Person{

    private final Date registrationDate;

    protected ArrayList<String> cardsIds; // pentru situatia cand scot un obiect din fisier/ din db si vreau sa retin id urile
    protected ArrayList<String> accountsIds;

    private HashMap<String, Card> cards;
    private HashMap<String, Account> accounts;

    public static Client loadClient(String clientId, HashMap<String, Employee> empDependencies) throws InterruptedException {

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        Client loaded = (Client) dbManager.loadObject(clientId, Client.class, null);

        Function<String, String> accGetEmpAssistantId = (new AccountDb())::getEmpAssistantId;
        Object[] getDepAccLoad = new Function[]{accGetEmpAssistantId};

        Employee empAssistant;
        String empAssistantId;

        if(empDependencies == null)
            empDependencies = new HashMap<>();

        for(String accountId : loaded.accountsIds){

            empAssistantId = (String) dbManager.loadObject(accountId, String.class, getDepAccLoad);

            if(empDependencies.get(empAssistantId) != null)
                empAssistant = empDependencies.get(empAssistantId);
            else {

                empAssistant = (Employee) dbManager.loadObject(empAssistantId, Employee.class, null);
                empDependencies.put(empAssistantId, empAssistant);
            }

            Object[] accDepLoad = new Object[]{empAssistant, loaded};
            //System.out.println(SavingsAccount.class);
            // trebuie sa incerc fiecare tabel sa vad ce fel de cont este

            Object[] a = new Object[4];
            a[0] = dbManager.loadObject(accountId, SavingsAccount.class, accDepLoad);
            a[1] = dbManager.loadObject(accountId, DepotAccount.class, accDepLoad);
            a[2] = dbManager.loadObject(accountId, BasicAccount.class, accDepLoad);
            a[3] = dbManager.loadObject(accountId, CurrentAccount.class, accDepLoad);

            int i;
            for(i = 0; i < 4; i++)
                if(a[i] != null)
                    break;

            loaded.accounts.put(accountId, (Account)a[i]);
        }

        Function<String, String> cardGetEmpAssistantId = (new CardDb())::getEmpAssistantId;
        Function<String, String> cardGetAssociatedAccount = (new CardDb())::getAssociatedAccountId;
        Object[] getDepCardLoad = new Function[]{cardGetEmpAssistantId, cardGetAssociatedAccount};

        return loaded;
    }

    public static Client newClient(String name, String surname, String birthDateStr){

        Client toReturn = new Client(name, surname, birthDateStr);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(toReturn, 2);

        return toReturn;
    }

    protected Client(String name, String surname, String birthDateStr, String address, String email, String phoneNumber,
                  String registrationDateStr, String id, ArrayList<String> accountsIds, ArrayList<String> cardsIds) {

        super(name, surname, birthDateStr, address, email, phoneNumber, id);

        if(!Validator.pastDateOk(registrationDateStr))
            throw new IllegalArgumentException("invalid constructor arguments");

        this.registrationDate = Date.valueOf(registrationDateStr);

        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();

        this.accountsIds = accountsIds;
        this.cardsIds = cardsIds;

        ClientsManager.clients.put(this.id, this);
    }

    public Client(String name, String surname, String birthDateStr) {
        super(name, surname, birthDateStr);

        this.registrationDate = new Date(System.currentTimeMillis());

        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();

        ClientsManager.clients.put(this.id, this);
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Account getAccount(String toSearch) {
        return accounts.get(toSearch);
    }

    public Iterator<HashMap.Entry<String, Account>> getAllAccounts(){
        return accounts.entrySet().iterator();
    }

    public Card getCard(String toSearch){
        return cards.get(toSearch);
    }

    public Iterator<HashMap.Entry<String, Card>> getAllCards(){
        return cards.entrySet().iterator();
    }

    public double getClientFunds(){

        long totalFunds = 0;

        Iterator<HashMap.Entry<String, Account>> accountIterator = getAllAccounts();

        while(accountIterator.hasNext()){

            HashMap.Entry<String, Account> strAcc = accountIterator.next();
            totalFunds += strAcc.getValue().getBalance();
        }

        return totalFunds;
    }

    public double getCreditDebt(){

        long totalCreditDebt = 0;

        Iterator<HashMap.Entry<String, Card>> cardIterator = getAllCards();

        while(cardIterator.hasNext()){

            HashMap.Entry<String, Card> strCard = cardIterator.next();
            if(strCard.getValue().getClass() == CreditCard.class)
                totalCreditDebt += strCard.getValue().getBalance();
        }

        return totalCreditDebt;
    }

    protected void linkAccount(Account toLink){
        this.accounts.put(toLink.getName(), toLink);
    }

    protected void linkCard(Card toLink){

        if(toLink instanceof DebitCard){

            AccountWithCard associatedAccount = (AccountWithCard) accounts.get(((DebitCard) toLink).getAccountName());
            associatedAccount.linkCard((DebitCard) toLink);
        }

        this.cards.put(toLink.getCardId(), toLink);
    }

    public BasicAccount createBasicAccount(String name, Employee contractAssistant) {

        IdGenerator idGenerator = IdGenerator.getIdGenerator();

        BasicAccount newBasicAccount = new BasicAccount(idGenerator.getAccountId("BASIC"), this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newBasicAccount, 2);

        return newBasicAccount;
    }

    public CurrentAccount createCurrentAccount(String name, Employee contractAssistant) {

        IdGenerator idGenerator = IdGenerator.getIdGenerator();

        CurrentAccount newCurrentAccount = new CurrentAccount(idGenerator.getAccountId("CURRENT"), this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newCurrentAccount, 2);

        return newCurrentAccount;
    }

    public SavingsAccount createSavingsAccount(String name, Employee contractAssistant) {

        IdGenerator idGenerator = IdGenerator.getIdGenerator();

        SavingsAccount newSavingsAccount = new SavingsAccount(idGenerator.getAccountId("SAVINGS"), this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newSavingsAccount, 2);

        return newSavingsAccount;
    }

    public DepotAccount createDepotAccount(String name, Employee contractAssistant, String type, double initialValue) {

        IdGenerator idGenerator = IdGenerator.getIdGenerator();

        DepotAccount newDepotAccount = new DepotAccount(idGenerator.getAccountId("DEPOT"), this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newDepotAccount, 2);

        return newDepotAccount;
    }

    public DebitCard createDebitCard(String accountName, String cardName, Employee contractAssistant) {

        Account toAssociateUnchecked = accounts.get(accountName);

        if(toAssociateUnchecked == null)
            throw new NullPointerException("associated account does not exist");

        if(toAssociateUnchecked.getClass() != BasicAccount.class && toAssociateUnchecked.getClass() != CurrentAccount.class)
            throw new IllegalArgumentException("associated account is of wrong type");

        AccountWithCard toAssociate = (AccountWithCard) toAssociateUnchecked;
        DebitCard newDebitCard = toAssociate.associateNewCard(cardName, contractAssistant);

        cards.put(cardName, newDebitCard);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newDebitCard, 2);

        return newDebitCard;
    }

    public CreditCard createCreditCard(String cardName, double requestedAmmount, Employee contractAssistant){

        IdGenerator idGenerator = IdGenerator.getIdGenerator();

        CreditCard newCreditCard = new CreditCard(this, idGenerator.getCardId("CREDIT"), cardName, contractAssistant, requestedAmmount);
        cards.put(cardName, newCreditCard);

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(newCreditCard, 2);

        return newCreditCard;
    }

    /*public BasicAccount createBasicAccount(String name, Employee contractAssistant, String accountId) {

        BasicAccount newBasicAccount = new BasicAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);

        return newBasicAccount;
    }

    public CurrentAccount createCurrentAccount(String name, Employee contractAssistant, String accountId) {

        CurrentAccount newCurrentAccount = new CurrentAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);

        return newCurrentAccount;
    }

    public SavingsAccount createSavingsAccount(String name, Employee contractAssistant, String accountId) {

        SavingsAccount newSavingsAccount = new SavingsAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);

        return newSavingsAccount;
    }

    public DepotAccount createDepotAccount(String name, Employee contractAssistant, String type, double initialValue, String accountId) {

        DepotAccount newDepotAccount = new DepotAccount(accountId, this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);

        return newDepotAccount;
    }

    public DebitCard createDebitCard(String accountName, String cardName, Employee contractAssistant, String cardId) {

        Account toAssociateUnchecked = accounts.get(accountName);

        if(toAssociateUnchecked == null)
            throw new NullPointerException("associated account does not exist");

        if(toAssociateUnchecked.getClass() != BasicAccount.class && toAssociateUnchecked.getClass() != CurrentAccount.class)
            throw new IllegalArgumentException("associated account is of wrong type");

        AccountWithCard toAssociate = (AccountWithCard) toAssociateUnchecked;
        DebitCard newDebitCard = toAssociate.associateNewCard(cardName, contractAssistant, cardId);

        cards.put(cardName, newDebitCard);

        return newDebitCard;
    }

    public CreditCard createCreditCard(String cardName, double requestedAmmount, Employee contractAssistant, String cardId){

        CreditCard newCreditCard = new CreditCard(this, cardId, cardName, contractAssistant, requestedAmmount);
        cards.put(cardName, newCreditCard);

        return newCreditCard;
    }*/

    public void removeDebitCard(String cardName){

        DebitCard toRemove = (DebitCard) cards.get(cardName);
        if(toRemove == null)
            throw new NullPointerException("card to remove does not exist");

        //AccountWithCard associatedAccount = (AccountWithCard) accounts.get(toRemove.getAccountName());
        //associatedAccount.removeCard();

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(toRemove, 3);

        cards.remove(cardName);
    }

    public void removeCreditCard(String cardName){

        CreditCard toRemove = (CreditCard) cards.get(cardName);
        if(toRemove == null)
            throw new NullPointerException("card to remove does not exist");

        toRemove.dereferenceCard();

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(toRemove, 3);

        cards.remove(cardName);
    }

    public void deleteAccount(String name){

        Account toRemove = accounts.get(name);

        if(toRemove == null)
            throw new NullPointerException("account to remove does not exist");

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(toRemove, 3);

        accounts.remove(name);

        if(toRemove instanceof AccountWithCard accountWithCard)
            cards.remove(accountWithCard.getAccountId());
    }

    protected String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());
        serialization.append("CLIENT: ");

        serialization.append(this.getRegistrationDate()).append(";");

        StringBuilder accounts = new StringBuilder("accounts: ");

        Iterator<HashMap.Entry<String, Account>> ita = this.getAllAccounts();

        while(ita.hasNext()){

            HashMap.Entry<String, Account> strAcc = ita.next();
            accounts.append(strAcc.getValue().getAccountId());
            accounts.append(",");
        }

        serialization.append(accounts.toString()).append(";");

        StringBuilder cards = new StringBuilder("cards: ");

        Iterator<HashMap.Entry<String, Card>> itc = this.getAllCards();

        while(itc.hasNext()){

            HashMap.Entry<String, Card> strAcc = itc.next();
            cards.append(strAcc.getValue().getCardId());
            cards.append(",");
        }

        serialization.append(cards.toString()).append(";");

        return serialization.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        Client client = (Client) o;
        return registrationDate.equals(client.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), registrationDate, accounts);
    }

    @Override
    public String toString() {
        return "Client{" +
                "registrationDate=" + registrationDate +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", id='" + id + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
