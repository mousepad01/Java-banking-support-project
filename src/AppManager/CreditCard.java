package AppManager;

/*
    nu va fi asociat vreunui cont
    va avea o taxa fixa de la inceput
 */

import java.sql.Date;

public class CreditCard extends Card{

    public static final double FEE;

    static{
        FEE = 0.05;
    }

    private boolean activeStatus;

    private final double creditTotalAmmount;
    private double creditAmmount;

    protected CreditCard(Client owner, String cardId, String name, Employee contractAssistant, double requestedAmmount) {
        super(name, owner, contractAssistant, cardId);

        this.creditTotalAmmount = requestedAmmount * (1 + FEE);
        this.creditAmmount = requestedAmmount;
        this.activeStatus = true;
    }

    protected CreditCard(boolean suspendedStatus, boolean pinIsInitialized, byte[] pinHash, Employee contractAssistant,
                         Client owner, String cardId, String name, Date emissionDate, boolean activeStatus,
                         double creditTotalAmmount, double creditAmmount){

        super(suspendedStatus, pinIsInitialized, pinHash, contractAssistant, owner, cardId,
                name, emissionDate);

        this.activeStatus = activeStatus;
        this.creditTotalAmmount = creditTotalAmmount;
        this.creditAmmount = creditAmmount;
    }

    protected void dereferenceCard(){

        if(!isActive()){}

        if(!okToClose()){}

        activeStatus = false;
        suspendedStatus = true;
    }

    public double add(double val){

        if(!isActive()){}

        if(isSuspended()){}

        if(val + creditAmmount > creditTotalAmmount){}

        creditAmmount += val;
        return creditAmmount;
    }

    public double extract(double val){

        if(!isActive()){}

        if(isSuspended()){}

        if(creditAmmount - val < 0){}

        creditAmmount -= val;
        return creditAmmount;
    }

    public double getBalance(){

        if(!isActive()){}

        return creditAmmount;
    }

    protected boolean okToClose(){

        if(!isActive()){}

        if(isSuspended())
            return false;

        return creditTotalAmmount - creditAmmount < 0.01; // ca sa nu am probleme cu zecimale foarte multe
    }

    public boolean isActive(){
        return activeStatus;
    }

    public String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());
        serialization.append("CREDIT CARD: ");

        serialization.append(activeStatus).append(";");
        serialization.append(creditTotalAmmount).append(";");
        serialization.append(creditAmmount).append(";");

        return serialization.toString();
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                super.toString() + "\n" +
                "activeStatus=" + activeStatus +
                ", creditTotalAmmount=" + creditTotalAmmount +
                ", creditAmmount=" + creditAmmount +
                '}';
    }
}
