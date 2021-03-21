package Rest;

import Rest.Account;

import java.sql.Date;
import java.util.HashMap;
import java.util.Objects;

public class Client extends Person {

    protected final Date registrationDate;

    protected HashMap<String, Account> accounts;

    public Client(String name, String surname, String birthDateStr, String address, String id, String email, String phoneNumber, String registrationDateStr) {
        super(name, surname, birthDateStr, address, id, email, phoneNumber);

        if(!Validator.pastDateOk(registrationDateStr)){}

        this.registrationDate = Date.valueOf(registrationDateStr);
        this.accounts = new HashMap<>();
    }

    public Client(String name, String surname, String birthDateStr, String id, String registrationDateStr) {
        super(name, surname, birthDateStr, id);

        if(!Validator.pastDateOk(registrationDateStr)){}

        this.registrationDate = Date.valueOf(registrationDateStr);
        this.accounts = new HashMap<>();
    }

    public Client(String name, String surname, String birthDateStr, String id) {
        super(name, surname, birthDateStr, id);

        this.registrationDate = new Date(System.currentTimeMillis());
        this.accounts = new HashMap<>();
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Account getAccount(String toSearch) {
        return accounts.get(toSearch);
    }

    public void deleteAccount(String name){

        Account toRemove = accounts.get(name);

        if(toRemove == null){};

        if(toRemove.getBalance() < 0){}

        accounts.remove(name);
    }

    public void createBasicAccount(String name, Employee contractAssistant){

        BasicAccount newBasicAccount = new BasicAccount(AccountIdGenerator.getId("BASIC"), this, name, contractAssistant);
        this.accounts.put(name, newBasicAccount);
    }

    public void createCurrentAccount(String name, Employee contractAssistant){

        CurrentAccount newCurrentAccount = new CurrentAccount(AccountIdGenerator.getId("CURRENT"), this, name, contractAssistant);
        this.accounts.put(name, newCurrentAccount);
    }

    public void createSavingsAccount(String name, Employee contractAssistant){

        SavingsAccount newSavingsAccount = new SavingsAccount(AccountIdGenerator.getId("SAVINGS"), this, name, contractAssistant);
        this.accounts.put(name, newSavingsAccount);
    }

    public void createDepotAccount(String name, Employee contractAssistant, String type, double initialValue){

        DepotAccount newDepotAccount = new DepotAccount(AccountIdGenerator.getId("DEPOT"), this, name, contractAssistant, type, initialValue);
        this.accounts.put(name, newDepotAccount);
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
