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

    private final double creditTotalAmount;
    private double creditAmount;

    protected CreditCard(Client owner, String cardId, String name, Employee contractAssistant, double requestedAmmount) {
        super(name, owner, contractAssistant, cardId);

        this.creditTotalAmount = requestedAmmount * (1 + FEE);
        this.creditAmount = requestedAmmount;
        this.activeStatus = true;
    }

    protected CreditCard(boolean suspendedStatus, boolean pinIsInitialized, byte[] pinHash, Employee contractAssistant,
                         Client owner, String cardId, String name, Date emissionDate, boolean activeStatus,
                         double creditTotalAmount, double creditAmount){

        super(suspendedStatus, pinIsInitialized, pinHash, contractAssistant, owner, cardId,
                name, emissionDate);

        this.activeStatus = activeStatus;
        this.creditTotalAmount = creditTotalAmount;
        this.creditAmount = creditAmount;
    }

    protected void dereferenceCard(){

        if(!isActive())
            throw new RuntimeException("card is not active");

        if(!okToClose())
            throw new RuntimeException("cannot close card");

        activeStatus = false;
        suspendedStatus = true;
    }

    public double add(double val){

        if(!isActive())
            throw new RuntimeException("card is not active");

        if(isSuspended())
            throw new RuntimeException("card is suspended");

        if(val + creditAmount > creditTotalAmount)
            throw new RuntimeException("value is too big");

        creditAmount += val;
        return creditAmount;
    }

    public double extract(double val){

        if(!isActive())
        throw new RuntimeException("card is not active");

        if(isSuspended())
            throw new RuntimeException("card is suspended");

        if(creditAmount - val < 0)
            throw new RuntimeException("value is negative");

        creditAmount -= val;
        return creditAmount;
    }

    public double getBalance(){

        if(!isActive())
            throw new RuntimeException("card is not active");

        return creditAmount;
    }

    protected boolean okToClose(){

        if(!isActive())
            throw new RuntimeException("card is not active");

        if(isSuspended())
            return false;

        return creditTotalAmount - creditAmount < 0.01; // ca sa nu am probleme cu zecimale foarte multe
    }

    public boolean isActive(){
        return activeStatus;
    }

    protected double getTotalAmount(){
        return creditTotalAmount;
    }

    protected double getAmount(){
        return creditAmount;
    }

    protected String getSerialization(){

        StringBuilder serialization = new StringBuilder(super.getSerialization());
        serialization.append("CREDIT CARD: ");

        serialization.append(activeStatus).append(";");
        serialization.append(creditTotalAmount).append(";");
        serialization.append(creditAmount).append(";");

        return serialization.toString();
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                super.toString() + "\n" +
                "activeStatus=" + activeStatus +
                ", creditTotalAmount=" + creditTotalAmount +
                ", creditAmount=" + creditAmount +
                '}';
    }
}
