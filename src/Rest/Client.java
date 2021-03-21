package Rest;

import java.sql.Date;
import java.util.HashMap;
import java.util.Objects;

public class Client extends Person {

    protected final Date registrationDate;

    protected HashMap<String, Card> cards;
    protected HashMap<String, Account> accounts;

    public Client(String name, String surname, String birthDateStr, String address, String id, String email, String phoneNumber, String registrationDateStr) {
        super(name, surname, birthDateStr, address, id, email, phoneNumber);

        if(!Validator.pastDateOk(registrationDateStr)){}

        this.registrationDate = Date.valueOf(registrationDateStr);

        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();
    }

    public Client(String name, String surname, String birthDateStr, String id, String registrationDateStr) {
        super(name, surname, birthDateStr, id);

        if(!Validator.pastDateOk(registrationDateStr)){}

        this.registrationDate = Date.valueOf(registrationDateStr);

        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();
    }

    public Client(String name, String surname, String birthDateStr, String id) {
        super(name, surname, birthDateStr, id);

        this.registrationDate = new Date(System.currentTimeMillis());

        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Account getAccount(String toSearch) {
        return accounts.get(toSearch);
    }

    public Card getCard(String toSearch){
        return cards.get(toSearch);
    }

    public void deleteAccount(String name){

        Account toRemove = accounts.get(name);

        if(toRemove == null){};

        if(toRemove.getBalance() < 0){}

        accounts.remove(name);
    }

    public void createBasicAccount(String name, Employee contractAssistant){

        BasicAccount newBasicAccount = new BasicAccount(IdGenerator.getAccountId("BASIC"), this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);
    }

    public void createCurrentAccount(String name, Employee contractAssistant){

        CurrentAccount newCurrentAccount = new CurrentAccount(IdGenerator.getAccountId("CURRENT"), this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);
    }

    public void createSavingsAccount(String name, Employee contractAssistant){

        SavingsAccount newSavingsAccount = new SavingsAccount(IdGenerator.getAccountId("SAVINGS"), this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);
    }

    public void createDepotAccount(String name, Employee contractAssistant, String type, double initialValue){

        DepotAccount newDepotAccount = new DepotAccount(IdGenerator.getAccountId("DEPOT"), this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);
    }

    public void createDebitCard(String accountName, String cardName, Employee contractAssistant){

        Account toAssociateUnchecked = accounts.get(accountName);

        if(toAssociateUnchecked == null){}

        if(toAssociateUnchecked.getClass() != BasicAccount.class && toAssociateUnchecked.getClass() != CurrentAccount.class){}

        AccountWithCard toAssociate = (AccountWithCard) toAssociateUnchecked;
        DebitCard newDebitCard = toAssociate.associateNewCard(cardName, contractAssistant);

        cards.put(cardName, newDebitCard);
    }

    public void createCreditCard(String cardName, double requestedAmmount, Employee contractAssistant){

        CreditCard newCreditCard = new CreditCard(this, IdGenerator.getCardId("CREDIT"), cardName, contractAssistant, requestedAmmount);
        cards.put(cardName, newCreditCard);
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
