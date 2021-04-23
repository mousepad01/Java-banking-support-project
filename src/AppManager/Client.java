package AppManager;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Client extends Person {

    private final Date registrationDate;

    private ArrayList<String> cardsIds; // pentru situatia cand scot un obiect din fisier/ din db si vreau sa retin id urile
    private ArrayList<String> accountsIds;

    private HashMap<String, Card> cards;
    private HashMap<String, Account> accounts;

    public Client(String name, String surname, String birthDateStr, String address, String email, String phoneNumber,
                  String registrationDateStr, String id, ArrayList<String> accountsIds, ArrayList<String> cardsIds) {

        super(name, surname, birthDateStr, address, email, phoneNumber, id);

        if(!Validator.pastDateOk(registrationDateStr)){}

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

    public BasicAccount createBasicAccount(String name, Employee contractAssistant){

        BasicAccount newBasicAccount = new BasicAccount(IdGenerator.getAccountId("BASIC"), this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);

        return newBasicAccount;
    }

    public CurrentAccount createCurrentAccount(String name, Employee contractAssistant){

        CurrentAccount newCurrentAccount = new CurrentAccount(IdGenerator.getAccountId("CURRENT"), this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);

        return newCurrentAccount;
    }

    public SavingsAccount createSavingsAccount(String name, Employee contractAssistant){

        SavingsAccount newSavingsAccount = new SavingsAccount(IdGenerator.getAccountId("SAVINGS"), this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);

        return newSavingsAccount;
    }

    public DepotAccount createDepotAccount(String name, Employee contractAssistant, String type, double initialValue){

        DepotAccount newDepotAccount = new DepotAccount(IdGenerator.getAccountId("DEPOT"), this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);

        return newDepotAccount;
    }

    public DebitCard createDebitCard(String accountName, String cardName, Employee contractAssistant){

        Account toAssociateUnchecked = accounts.get(accountName);

        if(toAssociateUnchecked == null){}

        if(toAssociateUnchecked.getClass() != BasicAccount.class && toAssociateUnchecked.getClass() != CurrentAccount.class){}

        AccountWithCard toAssociate = (AccountWithCard) toAssociateUnchecked;
        DebitCard newDebitCard = toAssociate.associateNewCard(cardName, contractAssistant);

        cards.put(cardName, newDebitCard);

        return newDebitCard;
    }

    public CreditCard createCreditCard(String cardName, double requestedAmmount, Employee contractAssistant){

        CreditCard newCreditCard = new CreditCard(this, IdGenerator.getCardId("CREDIT"), cardName, contractAssistant, requestedAmmount);
        cards.put(cardName, newCreditCard);

        return newCreditCard;
    }

    public BasicAccount createBasicAccount(String name, Employee contractAssistant, String accountId){

        BasicAccount newBasicAccount = new BasicAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);

        return newBasicAccount;
    }

    public CurrentAccount createCurrentAccount(String name, Employee contractAssistant, String accountId){

        CurrentAccount newCurrentAccount = new CurrentAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);

        return newCurrentAccount;
    }

    public SavingsAccount createSavingsAccount(String name, Employee contractAssistant, String accountId){

        SavingsAccount newSavingsAccount = new SavingsAccount(accountId, this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);

        return newSavingsAccount;
    }

    public DepotAccount createDepotAccount(String name, Employee contractAssistant, String type, double initialValue, String accountId){

        DepotAccount newDepotAccount = new DepotAccount(accountId, this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);

        return newDepotAccount;
    }

    public DebitCard createDebitCard(String accountName, String cardName, Employee contractAssistant, String cardId){

        Account toAssociateUnchecked = accounts.get(accountName);

        if(toAssociateUnchecked == null){}

        if(toAssociateUnchecked.getClass() != BasicAccount.class && toAssociateUnchecked.getClass() != CurrentAccount.class){}

        AccountWithCard toAssociate = (AccountWithCard) toAssociateUnchecked;
        DebitCard newDebitCard = toAssociate.associateNewCard(cardName, contractAssistant, cardId);

        cards.put(cardName, newDebitCard);

        return newDebitCard;
    }

    public CreditCard createCreditCard(String cardName, double requestedAmmount, Employee contractAssistant, String cardId){

        CreditCard newCreditCard = new CreditCard(this, cardId, cardName, contractAssistant, requestedAmmount);
        cards.put(cardName, newCreditCard);

        return newCreditCard;
    }

    public void removeDebitCard(String cardName){

        DebitCard toRemove = (DebitCard) cards.get(cardName);
        if(toRemove == null){}

        AccountWithCard associatedAccount = (AccountWithCard) accounts.get(toRemove.getAccountName());
        associatedAccount.removeCard();

        cards.remove(cardName);
    }

    public void removeCreditCard(String cardName){

        CreditCard toRemove = (CreditCard) cards.get(cardName);
        if(toRemove == null){}

        toRemove.dereferenceCard();

        cards.remove(cardName);
    }

    public void deleteAccount(String name){

        Account toRemove = accounts.get(name);

        if(toRemove == null){};

        if(toRemove.getBalance() < 0){}

        accounts.remove(name);
    }

    public String getSerialization(){

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
