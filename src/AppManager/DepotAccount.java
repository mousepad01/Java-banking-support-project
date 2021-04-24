package AppManager;

import java.sql.Date;
import java.util.Objects;

import static java.lang.Math.pow;

/*
    - depozitul va avea un term la care se va adauga dobanda

    - daca se va modifica balance intre timp, acel term nu va mai primi dobanda

    - daca se va suspenda contul, pe parcursul suspendarii si al term ului in care suspendarea a fost ridicata
      nu se va acorda dobanda

    - actualizarea dobanzii are loc cu ajutorul unui "lazy update":
      nu se va actualiza in timp real, ci se va tine cont care a fost ultima actualizare
      si in momentul oricarei operatii cu suma din cont,
      se va actualiza suma curenta la valoarea corespunzatoare

 */

public class DepotAccount extends Account{

    public static final long MILISEC_TO_DAY;
    public static double INTEREST_RATE; /// per term

    static{
        //MILISEC_TO_DAY = 24 * 3600 * 1000;
        MILISEC_TO_DAY = 30; // pentru testare
        INTEREST_RATE = 0.1;
    }

    private final int term; /// days
    private Date lastUpdatedTerm; /// va retine data ultimului term in care s-a updatat contul
                                    /// deci lastUpdatedTerm - (momentul depunerii initiale) % term == 0

    protected DepotAccount(String accountId, Client owner, String name, Employee contractAssistant, String type, double initialValue) {
        super(accountId, owner, name, contractAssistant);

        this.balance = initialValue;
        this.lastUpdatedTerm = creationDate;

        switch(type){

            case "ONE YEAR":
                term = 365;
                break;

            case "6 MONTHS":
                term = 180;
                break;

            case "ONE MONTH":
                term = 30;
                break;

            default:
                term = 1; /// EROARE
                break;
        }
    }

    protected DepotAccount(String accountId, Client owner, String name, Employee contractAssistant,
                           Date creationDate, double balance, byte flags, int term, Date lastUpdatedTerm){

        super(accountId, owner, name, contractAssistant, creationDate, balance, flags);

        this.term = term;
        this.lastUpdatedTerm = lastUpdatedTerm;
    }

    public double add(double val){

        if(isSuspended()){}

        updateBalance(true);

        if(val <= 0){}

        balance += val;

        return balance;
    }

    public double extract(double val){

        if(isSuspended()){}

        if(val <= 0){}

        updateBalance(true);

        if(balance - val < 0){}

        balance -= val;

        return balance;
    }

    public double send(double toSend, Account receiverAccountUnchecked){

        if(isSuspended()){}

        if(toSend <= 0){}

        if(receiverAccountUnchecked.getClass() != CurrentAccount.class){}
        CurrentAccount receiverAccount = (CurrentAccount) receiverAccountUnchecked;

        if(!receiverAccount.owner.equals(this.owner)){}

        updateBalance(true);

        if(this.balance - toSend < 0){}

        receiverAccount.add(toSend);
        this.balance -= toSend;

        return balance;
    }

    // voi folosi bitul al doilea din flags pentru a retine daca in intervalul curent de scadenta
    // au fost efectuate tranzactii (si deci dobanda pe acel interval nu va mai fi oferita)

    public boolean suspendedInterest(){
        if(isSuspended()){}
        return (flags & 2) == 2;
    }

    public void suspendInterest(){
        if(isSuspended()){}
        this.flags |= 2;
    }

    public void dropSuspendedInterest(){
        if(isSuspended()){}
        this.flags &= 0b11111101;
    }

    private void updateBalance(boolean modify){

        if(isSuspended())
            return;

        long currentTimeDays = System.currentTimeMillis() / MILISEC_TO_DAY;
        long lastUpdatedDays = lastUpdatedTerm.getTime() / MILISEC_TO_DAY;

        if(currentTimeDays - lastUpdatedDays >= term){

            if(suspendedInterest())
                lastUpdatedDays += term;

            if((currentTimeDays - lastUpdatedDays) % term == 0 || !modify)
                dropSuspendedInterest();

            long nrTerms = (currentTimeDays - lastUpdatedDays) / term;
            balance = balance * pow(1 + INTEREST_RATE, nrTerms);

            long lastUpdatedVal = currentTimeDays - ((currentTimeDays - lastUpdatedDays) % term);
            lastUpdatedTerm = new Date(lastUpdatedVal * MILISEC_TO_DAY);
        }
        else if (modify)
            suspendInterest();
    }

    public String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());

        serialization.append("DEPOT ACCOUNT: ");
        serialization.append(this.term).append(";");
        serialization.append(this.lastUpdatedTerm.toString()).append(";");

        return serialization.toString();
    }

    @Override
    public void suspendAccount(){
        updateBalance(false);
        this.flags |= 1;
    }

    @Override
    public boolean isSuspended(){
        if((flags & 1) == 1){

            long currentTimeDays = System.currentTimeMillis() / MILISEC_TO_DAY;
            long lastUpdatedDays = lastUpdatedTerm.getTime() / MILISEC_TO_DAY;

            long lastUpdatedVal = currentTimeDays - ((currentTimeDays - lastUpdatedDays) % term);
            lastUpdatedTerm = new Date(lastUpdatedVal * MILISEC_TO_DAY);

            return true;
        }
        return false;
    }

    @Override
    public void dropSuspended(){
        if(isSuspended()){

            long currentTimeDays = System.currentTimeMillis() / MILISEC_TO_DAY;
            long lastUpdatedDays = lastUpdatedTerm.getTime() / MILISEC_TO_DAY;

            if((currentTimeDays - lastUpdatedDays) % term != 0)
                suspendInterest();
            else
                dropSuspendedInterest();

        }
        this.flags &= 0b11111110;
    }

    @Override
    public double getBalance(){
        updateBalance(false);
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

        DepotAccount that = (DepotAccount) o;
        return term == that.term && Double.compare(that.INTEREST_RATE, INTEREST_RATE) == 0 && Objects.equals(lastUpdatedTerm, that.lastUpdatedTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), term, INTEREST_RATE, lastUpdatedTerm);
    }

    @Override
    public String toString() {
        return "DepotAccount{" +
                super.toString() + "\n" +
                "term=" + term +
                ", lastUpdatedTerm=" + lastUpdatedTerm +
                '}';
    }
}
