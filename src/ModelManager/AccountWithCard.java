package ModelManager;

import java.util.Objects;

public abstract class AccountWithCard extends Account{

    protected DebitCard associatedCard;

    // nu voi putea asocia carduri contului decat prin intermediul clientului
    // pot in schimb sa vad ce card este si sa il suspend sau sa ridic suspendarea sau sa il deasociez contului

    protected DebitCard associateNewCard(String name, Employee contractAssistant){

        if(associatedCard == null){

            associatedCard = new DebitCard(this, contractAssistant, name, IdGenerator.getCardId("DEBIT"));
            return associatedCard;
        }
        // else EXCEPTIE
        return null;
    }

    public DebitCard getAssociatedCard(){
        return associatedCard;
    }

    public void removeCard(){

        // mai intai suspend cardul pentru a nu mai putea fi folosit
        // dupa care de referentiez cont -> card

        associatedCard.dereferenceCard();
        associatedCard = null;
    }

    public AccountWithCard(String accountId, Client owner, String name, Employee contractAssistant) {
        super(accountId, owner, name, contractAssistant);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AccountWithCard that = (AccountWithCard) o;
        return Objects.equals(associatedCard, that.associatedCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), associatedCard);
    }

    @Override
    public String toString() {
        return getName();
    }
}
