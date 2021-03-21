package Rest;

import java.sql.Date;
import java.util.Objects;

public abstract class Account {

    protected final String accountId;
    protected final Client owner;
    protected final String name;

    protected final Employee contractAssistant;

    protected final Date creationDate;

    protected double balance;

    protected int flags; /// m-am gandit sa am un camp de "flag" uri de tip cont suspendat/nu
                         /// si niste getters care sa citeasca cu o masca pe biti din flags prop ceruta
                         /// motive: 1. economisire de memorie (decat sa am foarte multe bool uri)
                         /// 2. atat timp cat oricum accesez doar prin getters
                         /// nu va ingreuna aplicatia dpdv al usurintei de utilizare
                         /// 3. as putea decide sa folosesc/ sa nu mai folosesc unele flag uri
                         ///    fara a trebui sa schimb structura claselor la fel de mult
                         ///    si nici a (viitoarei) baze de date din spate
                         ///    (as putea asigura backwards compatibility)

    /*protected Account(String accountId, Client owner, String creationDateStr, String name, Employee contractAssistant) {

        if(!Validator.pastDateOk(creationDateStr) || !accountIdOk(accountId)){}

        this.accountId = accountId;
        this.owner = owner;
        this.creationDate = Date.valueOf(creationDateStr);
        this.name = name;
        this.contractAssistant = contractAssistant;
    }*/

    protected Account(String accountId, Client owner, String name, Employee contractAssistant) {

        if(!accountIdOk(accountId)){}

        this.accountId = accountId;
        this.owner = owner;
        this.creationDate = new Date(System.currentTimeMillis());
        this.name = name;
        this.contractAssistant = contractAssistant;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isSuspended(){
        return (flags & 1) == 1;
    }

    public void suspendAccount(){
        this.flags |= 1;
    }

    public void dropSuspended(){
        this.flags &= 0b11111111111111111111111111111110;
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

    /// local validator(s)

    private boolean accountIdOk(String toCheck){
        return toCheck.matches("[a-z]{2}[0-9]{8}");
    }
}
