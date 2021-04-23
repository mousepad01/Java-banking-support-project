package AppManager;

/* clasa pe post de obiect temporar
 * in urma extragerii din fisiere sau din db */

import java.sql.Date;

public record UnlinkedCard(

        String type, // CREDIT/DEBIT

        boolean suspendedStatus,

        boolean pinIsInitialized,
        byte[] pinHash,

        String contractAssistantId,

        String ownerId,
        String cardId,
        String name,
        Date emissionDate,

        // atribute specifice creditCard

        boolean activeStatus,

        double creditTotalAmmount,
        double creditAmmount,

        // atribute specifice debitCard

        String associatedAccountId) {}
