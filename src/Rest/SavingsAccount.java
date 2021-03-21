package Rest;

import java.sql.Date;

/*
    - contul de economii va avea o dobanda care se adauga zilnic

    - pe parcursul intervalului cat contul este suspendat, dobanda nu se va mai adauga

    - dobanda depinde de invervalele in care suma din cont se afla

    - la fel ca si la depozit, actualizarea dobanzii are loc cu ajutorul unui "lazy update":
      nu se va actualiza in timp real, ci se va tine cont care a fost ultima actualizare
      si in momentul oricarei operatii cu suma din cont,
      se va actualiza suma curenta la valoarea corespunzatoare
 */

public class SavingsAccount extends Account{

    public static final long MILISEC_TO_DAY;
    public static double LOW_INTEREST;
    public static double LOW_INTEREST_THRESHOLD;
    public static double MID_INTEREST;
    public static double MID_INTEREST_THRESHOLD;
    public static double HIGH_INTEREST;

    static{
        //MILISEC_TO_DAY = 24 * 3600 * 1000;
        MILISEC_TO_DAY = 1000; /// pentru a putea testa
        LOW_INTEREST = 10;
        MID_INTEREST = 5;
        HIGH_INTEREST = 2;
        LOW_INTEREST_THRESHOLD = 10000;
        MID_INTEREST_THRESHOLD = 100000;
    }

    protected double interestRate; /// yearly
    protected Date lastUpdated;

    protected SavingsAccount(String accountId, Client owner, String name, Employee contractAssistant) {
        super(accountId, owner, name, contractAssistant);
    }

    private void updateInterestRate(){

        if(balance < LOW_INTEREST_THRESHOLD){
            interestRate = LOW_INTEREST;
        }
        else if(balance < MID_INTEREST_THRESHOLD){
            interestRate = MID_INTEREST;
        }
        else
            interestRate = HIGH_INTEREST;
    }

    public double add(double val){

        if(isSuspended()){}

        updateBalance();

        if(val <= 0){}

        balance += val;
        updateInterestRate();

        return balance;
    }

    public double extract(double val){

        if(isSuspended()){}

        if(val <= 0){}

        updateBalance();

        if(balance - val < 0){}

        balance -= val;
        updateInterestRate();

        return balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended()){}

        if(toSend <= 0){}

        if(receiverAccountUnchecked.getClass() != CurrentAccount.class){}
        CurrentAccount receiverAccount = (CurrentAccount) receiverAccountUnchecked;

        if(!receiverAccount.owner.equals(this.owner)){}

        updateBalance();

        if(this.balance - toSend < 0){}

        receiverAccount.add(toSend);
        this.balance -= toSend;

        updateInterestRate();

        return balance;
    }

    private void updateBalance(){

        long currentTime = System.currentTimeMillis();

        if(lastUpdated != null && !isSuspended()){

            double dailyInterestRate = interestRate / 365;

            long diff = (currentTime - lastUpdated.getTime()) / MILISEC_TO_DAY;
            for(long day = 0; day < diff; day++){

                balance *= (1 + dailyInterestRate);
                updateInterestRate();
            }
        }

        lastUpdated = new Date(currentTime);
    }

    @Override
    public void suspendAccount(){
        updateBalance();
        this.flags |= 1;
    }

    @Override
    public void dropSuspended(){
        if((flags & 1) == 1)
            lastUpdated = new Date(System.currentTimeMillis());
        this.flags &= 0b11111110;
    }

    @Override
    public double getBalance(){
        updateBalance();
        return balance;
    }
}
