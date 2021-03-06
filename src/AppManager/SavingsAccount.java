package AppManager;

import java.sql.Date;
import java.util.Objects;

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

    private double interestRate; /// yearly
    private Date lastUpdated;

    protected SavingsAccount(String accountId, Client owner, String name, Employee contractAssistant) {
        super(accountId, owner, name, contractAssistant);
    }

    protected SavingsAccount(String accountId, Client owner, String name, Employee contractAssistant,
                           Date creationDate, double balance, byte flags, double interestRate, Date lastUpdated){

        super(accountId, owner, name, contractAssistant, creationDate, balance, flags);

        this.interestRate = interestRate;
        this.lastUpdated = lastUpdated;
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

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    public double add(double val){

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        updateBalance();

        if(val <= 0)
            throw new RuntimeException("value is negative");

        balance += val;
        updateInterestRate();

        return balance;
    }

    public double extract(double val){

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        if(val <= 0)
            throw new RuntimeException("value is negative");

        updateBalance();

        if(balance - val < 0)
            throw new RuntimeException("value is negative");

        balance -= val;
        updateInterestRate();

        return balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended())
            throw new RuntimeException("account is suspended");

        if(toSend <= 0)
            throw new RuntimeException("value is negative");

        if(receiverAccountUnchecked.getClass() != CurrentAccount.class)
            throw new IllegalArgumentException("receiver account is of wrong type");

        CurrentAccount receiverAccount = (CurrentAccount) receiverAccountUnchecked;

        if(!receiverAccount.owner.equals(this.owner))
            throw new IllegalArgumentException("receiver account does not have the same owner");

        updateBalance();

        if(this.balance - toSend < 0)
            throw new RuntimeException("value is negative");

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

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    protected String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());

        serialization.append("SAVINGS ACCOUNT: ");
        serialization.append(this.interestRate).append(";");
        serialization.append(this.lastUpdated.toString()).append(";");

        return serialization.toString();
    }

    public double getInterestRate(){
        return this.interestRate;
    }

    public Date getLastUpdated(){
        return this.lastUpdated;
    }

    @Override
    public void suspendAccount(){
        updateBalance();
        this.flags |= 1;

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    @Override
    public void dropSuspended(){
        if((flags & 1) == 1)
            lastUpdated = new Date(System.currentTimeMillis());
        this.flags &= 0b11111110;

        DbManager dbManager = DbManager.getDbManger(Thread.currentThread().getId());
        dbManager.setChange(this, 1);
    }

    @Override
    public double getBalance(){
        updateBalance();
        return balance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        SavingsAccount that = (SavingsAccount) o;
        return Double.compare(that.interestRate, interestRate) == 0 && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interestRate, lastUpdated);
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                super.toString() + "\n" +
                "interestRate=" + interestRate +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
