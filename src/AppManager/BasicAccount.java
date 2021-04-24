package AppManager;
/*
    cont de baza, are doar o suma din care pot extrage sau pot adauga
    asemanator contului curent
 */

import java.sql.Date;
import java.util.Objects;

public class BasicAccount extends AccountWithCard {

    public static double MAX_BALANCE;
    public static double TRANSACTION_FEE;

    static {
        MAX_BALANCE = 7000;
        TRANSACTION_FEE = 0.01;
    }

    protected BasicAccount(String accountId, Client owner, String name, Employee contractAssistant) {
        super(accountId, owner, name, contractAssistant);
    }

    protected BasicAccount(String accountId, Client owner, String name, Employee contractAssistant,
                           Date creationDate, double balance, byte flags, DebitCard associatedCard){

        super(accountId, owner, name, contractAssistant, creationDate, balance, flags, associatedCard);
    }

    public double add(double val){

        if(isSuspended()){}

        if(val <= 0){}

        if(val + this.balance > MAX_BALANCE){}

        this.balance += val;
        return this.balance;
    }

    public double extract(double val){

        if(isSuspended()){}

        if(val <= 0){}

        if(this.balance - val < 0){}

        this.balance -= val;
        return this.balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended()){}

        if(receiverAccountUnchecked.getClass() != getClass()){}
        BasicAccount receiverAccount = (BasicAccount) receiverAccountUnchecked;

        if(toSend <= 0){}

        if(receiverAccount.getClass() != getClass()){}

        double fee = TRANSACTION_FEE;

        if(receiverAccount.owner.equals(this.owner)){
            fee = 0;
        }

        if(this.balance - (toSend * (1 + fee)) < 0){}

        if(toSend + receiverAccount.balance > MAX_BALANCE){}

        receiverAccount.add(toSend);
        this.balance -= toSend * (1 + fee);

        return balance;
    }

    public String getSerialization(){

        String serialization = super.getSerialization();

        serialization += "BASIC ACCOUNT: ;";

        return serialization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        BasicAccount that = (BasicAccount) o;
        return balance == that.balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), balance);
    }

    @Override
    public String toString() {
        return "BasicAccount{" + super.toString() + "\n" + "}";
    }
}
