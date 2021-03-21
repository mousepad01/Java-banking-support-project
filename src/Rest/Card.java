package Rest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/*
    la creerea cardului, se va asigna un pin random
    acesta va putea fi extras o singura data
    cu ajutorul unei metode protected care retine de cate ori a fost apelata
 */

public abstract class Card {

    public boolean suspendedStatus;

    private boolean pinIsInitialized;
    private byte[] pinHash;

    protected final Employee contractAssistant;

    protected final Client owner;
    protected final String cardId;
    protected final String name;
    protected final Date emissionDate;

    protected Card(String name, Client owner, Employee contractAssistant, String cardId) {

        this.contractAssistant = contractAssistant;
        this.owner = owner;
        this.cardId = cardId;
        this.name = name;
        this.emissionDate = new Date(System.currentTimeMillis());

        pinIsInitialized = false;
    }

    public abstract double add(double val);

    public abstract double extract(double val);

    public abstract double getBalance();

    public String getName(){
        return name;
    }

    public boolean isSuspended(){
        return suspendedStatus;
    }

    protected void suspendCard(){
        suspendedStatus = true;
    }

    protected void dropSuspended(){
        suspendedStatus = false;
    }

    protected int initPin(){

        if(pinIsInitialized){}

        Random randgen = new Random(System.currentTimeMillis());
        int pin = randgen.nextInt(8999) + 1000;
        pinHash = getHash(pin);

        pinIsInitialized = true;

        return pin;
    }

    public void changePin(int currentPin, int newPin){

        if(!checkPin(currentPin)){}

        pinHash = getHash(newPin);
    }

    public Client getOwner(){
        return owner;
    }

    public Date getEmissionDate(){
        return emissionDate;
    }

    public String getCardId(){
        return cardId;
    }

    public Employee getContractAssistant(){
        return contractAssistant;
    }

    private byte[] getHash(int val){

        String valString = String.valueOf(val);

        try {
            MessageDigest makeHash = MessageDigest.getInstance("SHA-256");
            return makeHash.digest(valString.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
            return null;
        }
    }

    private boolean checkPin(int pinUncheckedInt){

        byte[] uncheckedHash = getHash(pinUncheckedInt);

        for(int i = 0; i < uncheckedHash.length; i++)
            if(pinHash[i] != uncheckedHash[i])
                return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Card card = (Card) o;
        return suspendedStatus == card.suspendedStatus && pinIsInitialized == card.pinIsInitialized && Arrays.equals(pinHash, card.pinHash) && contractAssistant.equals(card.contractAssistant) && owner.equals(card.owner) && cardId.equals(card.cardId) && name.equals(card.name) && emissionDate.equals(card.emissionDate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(suspendedStatus, pinIsInitialized, contractAssistant, owner, cardId, name, emissionDate);
        result = 31 * result + Arrays.hashCode(pinHash);
        return result;
    }

    @Override
    public String toString() {
        return "Card{" +
                "suspendedStatus=" + suspendedStatus +
                ", contractAssistant=" + contractAssistant +
                ", owner=" + owner +
                ", cardId='" + cardId + '\'' +
                ", name='" + name + '\'' +
                ", emissionDate=" + emissionDate +
                '}';
    }
}
