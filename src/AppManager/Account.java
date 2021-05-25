package AppManager;

import java.sql.Date;
import java.util.Objects;

public abstract class Account {

    protected final String accountId;
    protected final Client owner;
    protected final String name;

    protected final Employee contractAssistant;

    protected final Date creationDate;

    protected double balance;

    protected byte flags; /// m-am gandit sa am un camp de "flag" uri de tip cont suspendat/nu
                          /// si niste getters care sa citeasca cu o masca pe biti din flags prop ceruta
                          /// motive: 1. economisire de memorie (decat sa am foarte multe bool uri)
                          /// 2. atat timp cat oricum accesez doar prin getters
                          /// nu va ingreuna aplicatia dpdv al usurintei de utilizare
                          /// 3. as putea decide sa folosesc/ sa nu mai folosesc unele flag uri
                          ///    fara a trebui sa schimb structura claselor la fel de mult
                          ///    si nici a (viitoarei) baze de date din spate
                          ///    (as putea asigura backwards compatibility)

    protected Account(String accountId, Client owner, String name, Employee contractAssistant) {

        if(contractAssistant == null)
            throw new NullPointerException("employee is not valid");

        this.accountId = accountId;
        this.owner = owner;
        this.creationDate = new Date(System.currentTimeMillis());
        this.name = name;
        this.contractAssistant = contractAssistant;

        Logger log = Logger.getLogger();
        log.logMessage("new account created");
    }

    protected Account(String accountId, Client owner, String name, Employee contractAssistant,
                      Date creationDate, double balance, byte flags){

        this.accountId = accountId;
        this.owner = owner;
        this.name = name;
        this.contractAssistant = contractAssistant;
        this.creationDate = creationDate;
        this.balance = balance;
        this.flags = flags;

        Logger log = Logger.getLogger();
        log.logMessage("new account created");
    }

    public abstract double add(double val);

    public abstract double extract(double val);

    public abstract double send(double toSend, Account receiverAccountUnchecked);

    public double getBalance() {
        return balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public Client getOwner() {
        return owner;
    }

    public String getName(){
        return name;
    }

    protected Byte getFlags(){
        return flags;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isSuspended(){
        return (flags & 1) == 1;
    }

    public void suspendAccount(){
        this.flags |= 1;

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    public void dropSuspended(){
        this.flags &= 0b11111110;

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    public Employee getContractAssistant(){
        return contractAssistant;
    }

    protected String getSerialization(){

        StringBuilder serialization = new StringBuilder("ACCOUNT: ");

        serialization.append(this.getAccountId()).append(";");
        serialization.append(this.getOwner().getId()).append(";");
        serialization.append(this.getName()).append(";");
        serialization.append(this.getContractAssistant().getId()).append(";"); // account assistant tot timpul nenul
        serialization.append(this.getCreationDate().toString()).append(";");
        serialization.append(this.getBalance()).append(";");
        serialization.append(this.flags).append(";");

        return serialization.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Account account = (Account) o;
        return flags == account.flags && accountId.equals(account.accountId) && owner.equals(account.owner) && name.equals(account.name) && creationDate.equals(account.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, owner, name, creationDate, flags);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", owner=" + owner.toString() +
                ", name='" + name + '\'' +
                ", contractAssistant=" + contractAssistant.getId() +
                ", creationDate=" + creationDate +
                ", balance=" + balance +
                ", flags=" + flags +
                '}';
    }
}
