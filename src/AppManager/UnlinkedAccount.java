package AppManager;

/* clasa record pe post de obiect temporar
 * in urma extragerii din fisiere sau din db */

import java.sql.Date;

public record UnlinkedAccount(

         String type, // Basic/Current/etc
         String accountId,
         String ownerId,
         String name,

         String contractAssistantId,

         Date creationDate,

         double balance,

         byte flags,

        // atribute specifice accountWithCard

         String associatedCardId,

        // atribute specifice currentAccount

         double transactionFee,
         double extractFee,
         double addFee,

        // atribute specifice savingsAccount

         double interestRate,
         Date lastUpdated,

        // atribute specifice depotAccount
         int term,
         Date lastUpdatedTerm) {}
