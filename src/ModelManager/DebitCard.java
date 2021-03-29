package ModelManager;

/*
    - este asociat unui cont basic / curent si toate tranzactiile se leaga de balance ul acelui cont
    - cu o instanta a cardului pot extrage doar numele contului asociat
      dar cu o instanta a contului asociat pot extrage o referinta catre card
 */

public class DebitCard extends Card{

    private Account associatedAccount;

    protected DebitCard(Account associatedAccount, Employee contractAssistant, String name, String cardId) {
        super(name, associatedAccount.getOwner(), contractAssistant, cardId);

        this.associatedAccount = associatedAccount;
    }

    // poate fi apelata doar prin metoda din cont
    protected void dereferenceCard(){

        associatedAccount = null;
        suspendedStatus = true;
    }

    public boolean isValid(){
        return associatedAccount == null;
    }

    /// toate actiunile vor fi incapsulate in blocuri try catch

    public double add(double val){

        if(associatedAccount == null){}

        if(isSuspended()){}

        associatedAccount.add(val);
        return associatedAccount.getBalance();
    }

    public double extract(double val){

        if(associatedAccount == null){}

        if(isSuspended()){}

        associatedAccount.extract(val);
        return associatedAccount.getBalance();
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(associatedAccount == null){}

        if(isSuspended()){}

        associatedAccount.send(toSend, receiverAccountUnchecked);
        return associatedAccount.getBalance();
    }

    public double getBalance(){

        if(associatedAccount == null){}

        return associatedAccount.getBalance();
    }

    public String getAccountName(){

        if(associatedAccount == null){}

        return associatedAccount.getName();
    }
}
