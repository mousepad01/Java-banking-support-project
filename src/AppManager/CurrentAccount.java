package AppManager;

import java.sql.Date;
import java.util.Objects;

public class CurrentAccount extends AccountWithCard{
    
    public static double MAX_BALANCE;

    static{
        MAX_BALANCE = 200000;
    }

    private double transactionFee;
    private double extractFee;
    private double addFee;

    protected CurrentAccount(String accountId, Client owner, String name, Employee contractAssistant) {
        super(accountId, owner, name, contractAssistant);

        this.transactionFee = 0.1;
        this.addFee = 0.01;
        this.extractFee = 0.05;
    }

    protected CurrentAccount(String accountId, Client owner, String name, Employee contractAssistant,
                             Date creationDate, double balance, byte flags, DebitCard associatedCard,
                             double transactionFee, double extractFee, double addFee){

        super(accountId, owner, name, contractAssistant, creationDate, balance, flags, associatedCard);

        this.transactionFee = transactionFee;
        this.extractFee = extractFee;
        this.addFee = addFee;
    }

    private void updateFees(){

        if(balance > 50000){
            transactionFee = 0.02;
            extractFee = 0.015;
            addFee = 0.004;
        }
        else{
            transactionFee = 0.1;
            addFee = 0.01;
            extractFee = 0.05;
        }
    }

    public double add(double val){

        if(isSuspended()){}

        if(val <= 0){}

        if((val * (1 + addFee)) + balance > MAX_BALANCE){}

        balance += val;
        updateFees();

        return balance;
    }

    public double extract(double val){

        if(isSuspended()){}

        if(val <= 0){}

        if(balance - val - balance * extractFee < 0){}

        balance -= val;
        updateFees();

        return balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended()){}

        if(toSend <= 0){}

        if(receiverAccountUnchecked.getClass() != getClass()){}
        CurrentAccount receiverAccount = (CurrentAccount) receiverAccountUnchecked;

        double fee = transactionFee;

        if(receiverAccount.owner.equals(this.owner)){
            fee = 0;
        }

        if(this.balance - (toSend * (1 + fee)) < 0){}

        if(toSend + receiverAccount.balance > MAX_BALANCE){}

        receiverAccount.add(toSend);
        this.balance -= toSend * (1 + fee);

        updateFees();

        return this.balance;
    }

    public String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());

        serialization.append("CURRENT ACCOUNT: ");
        serialization.append(this.transactionFee).append(";");
        serialization.append(this.extractFee).append(";");
        serialization.append(this.addFee).append(";");

        return serialization.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionFee, extractFee, addFee);
    }

    @Override
    public String toString() {
        return "CurrentAccount{" +
                super.toString() + "\n" +
                "transactionFee=" + transactionFee +
                ", extractFee=" + extractFee +
                ", addFee=" + addFee +
                '}';
    }
}
