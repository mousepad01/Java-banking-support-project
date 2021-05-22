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

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        if(val <= 0)
            throw new RuntimeException("value is negative");

        if(val + this.balance > MAX_BALANCE)
            throw new RuntimeException("value is too big");

        this.balance += val;
        return this.balance;
    }

    public double extract(double val){

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        if(val <= 0)
            throw new RuntimeException("value is negative");

        if(this.balance - val < 0)
            throw new RuntimeException("value is negative");

        this.balance -= val;
        return this.balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        if(receiverAccountUnchecked.getClass() != getClass())
            throw new IllegalArgumentException("receiver account is of wrong type");

        BasicAccount receiverAccount = (BasicAccount) receiverAccountUnchecked;

        if(toSend <= 0)
            throw new RuntimeException("value is negative");

        double fee = TRANSACTION_FEE;

        if(receiverAccount.owner.equals(this.owner)){
            fee = 0;
        }

        if(this.balance - (toSend * (1 + fee)) < 0)
            throw new RuntimeException("value is negative");

        if(toSend + receiverAccount.balance > MAX_BALANCE)
            throw new RuntimeException("value is too big");

        receiverAccount.add(toSend);
        this.balance -= toSend * (1 + fee);

        return balance;
    }

    protected String getSerialization(){

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
