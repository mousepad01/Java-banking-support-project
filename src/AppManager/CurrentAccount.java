package AppManager;

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

    protected double getTransactionFee() {
        return transactionFee;
    }

    protected double getExtractFee() {
        return extractFee;
    }

    protected double getAddFee() {
        return addFee;
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
        return getName();
    }
}
