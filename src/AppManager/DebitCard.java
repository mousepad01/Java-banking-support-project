package AppManager;

/*
    - este asociat unui cont basic / curent si toate tranzactiile se leaga de balance ul acelui cont
    - cu o instanta a cardului pot extrage doar numele contului asociat
      dar cu o instanta a contului asociat pot extrage o referinta catre card
 */

import java.sql.Date;
import java.util.Arrays;

public class DebitCard extends Card{

    private AccountWithCard associatedAccount;

    protected DebitCard(AccountWithCard associatedAccount, Employee contractAssistant, String name, String cardId) {
        super(name, associatedAccount.getOwner(), contractAssistant, cardId);

        this.associatedAccount = associatedAccount;
    }

    protected DebitCard(boolean suspendedStatus, boolean pinIsInitialized, byte[] pinHash, Employee contractAssistant,
                        Client owner, String cardId, String name, Date emissionDate, AccountWithCard associatedAccount){

        super(suspendedStatus, pinIsInitialized, pinHash, contractAssistant, owner, cardId,
                name, emissionDate);

        this.associatedAccount = associatedAccount;
    }

    protected void dereferenceCard(){

        associatedAccount = null;
        suspendedStatus = true;
    }

    public boolean isValid(){
        return associatedAccount == null;
    }

    public double add(double val){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        if(isSuspended())
            throw new RuntimeException("card is suspended");

        associatedAccount.add(val);
        return associatedAccount.getBalance();
    }

    public double extract(double val){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        if(isSuspended())
            throw new RuntimeException("card is suspended");

        associatedAccount.extract(val);
        return associatedAccount.getBalance();
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        if(isSuspended())
            throw new RuntimeException("card is suspended");

        associatedAccount.send(toSend, receiverAccountUnchecked);
        return associatedAccount.getBalance();
    }

    public double getBalance(){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        return associatedAccount.getBalance();
    }

    public String getAccountName(){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        return associatedAccount.getName();
    }

    protected AccountWithCard getAccount(){

        if(associatedAccount == null)
            throw new NullPointerException("no associated account");

        return associatedAccount;
    }

    protected String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());
        serialization.append("DEBIT CARD: ");

        serialization.append(associatedAccount == null ? null : associatedAccount.getAccountId()).append(";");

        return serialization.toString();
    }

    @Override
    public String toString() {
        return "DebitCard{" +
                super.toString() + "\n" +
                "associatedAccount=" + (associatedAccount == null ? null : associatedAccount.getAccountId()) +
                '}';
    }
}
