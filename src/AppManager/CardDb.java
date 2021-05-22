package AppManager;

import java.sql.*;

public class CardDb {

    private static final char[] HEX_CHAR = "0123456789ABCDEF".toCharArray();

    private static String toHexRepr(byte[] toConvert){

        if(toConvert == null)
            return null;

        char[] repr = new char[toConvert.length * 2];
        for(int i = 0; i < toConvert.length; i++){

            int val = toConvert[i] & 0xFF;

            repr[2 * i + 1] = HEX_CHAR[val & 0xF];
            repr[2 * i] = HEX_CHAR[val >>> 4];
        }

        return new String(repr);
    }

    private static byte[] fromHexRepr(String toConvert){

        if(toConvert == null)
            return null;

        byte[] arr = new byte[toConvert.length() / 2];
        for(int i = 0; i < toConvert.length(); i += 2)
            arr[i / 2] = (byte)((Character.digit(toConvert.charAt(i), 16) << 4) | Character.digit(toConvert.charAt(i + 1), 16));

        return arr;
    }

    private void add(Card toAdd) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO card(id, emp_assistant_id, owner_id, name, emission_date, " +
                                                "pin_is_init, suspended_status, pin_hash) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getCardId());
            preparedStatement.setString(2, toAdd.getContractAssistant().getId());
            preparedStatement.setString(3, toAdd.getOwner().getId());
            preparedStatement.setString(4, toAdd.getName());
            preparedStatement.setDate(5, toAdd.getEmissionDate());
            preparedStatement.setInt(6, toAdd.isPinInitialized() ? 1:0);
            preparedStatement.setInt(7, toAdd.isSuspended() ? 1:0);
            preparedStatement.setString(8, toHexRepr(toAdd.getPinHash()));

            preparedStatement.execute();
        }
    }

    protected void add(CreditCard toAdd) throws SQLException {

        this.add((Card) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO credit_card(id, active_status, total_amount, credit_amount) " +
                                "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getCardId());
            preparedStatement.setInt(2, toAdd.isActive() ? 1:0);
            preparedStatement.setDouble(3, toAdd.getTotalAmount());
            preparedStatement.setDouble(4, toAdd.getAmount());

            preparedStatement.execute();
        }
    }

    protected void add(DebitCard toAdd) throws SQLException {

        this.add((Card) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO debit_card (id, account_id) " +
                                "VALUES (?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getCardId());
            preparedStatement.setString(2, toAdd.getAccount().getAccountId());

            preparedStatement.execute();
        }
    }

    private void update(Card toUpdate) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE card \n" +
                                "SET pin_is_init = ?, suspended_status = ?, pin_hash = ?" +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setInt(1, toUpdate.isPinInitialized() ? 1 : 0);
            preparedStatement.setInt(2, toUpdate.isSuspended() ? 1 : 0);
            preparedStatement.setString(3, toHexRepr(toUpdate.getPinHash()));
            preparedStatement.setString(4, toUpdate.getCardId());

            preparedStatement.execute();
        }
    }

    protected void update(CreditCard toUpdate) throws SQLException {

        this.update((Card) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE credit_card \n" +
                                "SET active_status = ?, credit_amount = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setInt(1, toUpdate.isActive() ? 1 : 0);
            preparedStatement.setDouble(2, toUpdate.getAmount());
            preparedStatement.setString(3, toUpdate.getCardId());

            preparedStatement.execute();
        }
    }

    protected void update(DebitCard toUpdate) throws SQLException {

        this.update((Card) toUpdate);
    }

    private void delete(Card toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM card \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getCardId());

            preparedStatement.execute();
        }
    }

    protected void delete(CreditCard toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM credit_card \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getCardId());

            preparedStatement.execute();
        }

        this.delete((Card) toDelete);
    }

    protected void delete(DebitCard toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE debit_card \n" +
                                "SET account_id = NULL " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getCardId());

            preparedStatement.execute();

            toExecute = "DELETE FROM debit_card \n" +
                        "WHERE id = ?";

            preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getCardId());

            preparedStatement.execute();
        }

        this.delete((Card) toDelete);
    }

    protected CreditCard loadCreditCard(String id, Client cardOwner, Employee empAssistant) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT c.*, cr.active_status, cr.total_amount, cr.credit_amount\n" +
                                "FROM card c, credit_card cr\n" +
                                "WHERE c.id = cr.id\n" +
                                "AND cr.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultCard = preparedStatement.executeQuery();

            if(resultCard.next()){

                String cardId = resultCard.getString("id");

                String empAssistantId = resultCard.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                String cardOwnerId = resultCard.getString("owner_id");
                if(!cardOwnerId.equals(cardOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String cardName = resultCard.getString("name");
                Date cardEmissionDate = resultCard.getDate("emission_date");
                boolean cardPinIsInitialized = resultCard.getInt("pin_is_init") == 1;
                boolean cardSuspendedStatus = resultCard.getInt("suspended_status") == 1;
                byte[] cardPinHash = fromHexRepr(resultCard.getString("pin_hash"));

                boolean cardActiveStatus = resultCard.getInt("active_status") == 1;
                double cardTotalCreditAmount = resultCard.getDouble("total_amount");
                double cardCreditAmount = resultCard.getDouble("credit_amount");

                return new CreditCard(cardSuspendedStatus, cardPinIsInitialized, cardPinHash, empAssistant,
                                        cardOwner, cardId, cardName, cardEmissionDate, cardActiveStatus,
                                        cardTotalCreditAmount, cardCreditAmount);
            }

            return null;
        }
    }

    protected DebitCard loadDebitCard(String id, Client cardOwner, Employee empAssistant,
                                     AccountWithCard associatedAccount) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT c.*, d.account_id\n" +
                                "FROM card c, debit_card d\n" +
                                "WHERE c.id = d.id\n" +
                                "AND d.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultCard = preparedStatement.executeQuery();

            if(resultCard.next()){

                String cardId = resultCard.getString("id");

                String empAssistantId = resultCard.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                String cardOwnerId = resultCard.getString("owner_id");
                if(!cardOwnerId.equals(cardOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String cardName = resultCard.getString("name");
                Date cardEmissionDate = resultCard.getDate("emission_date");
                boolean cardPinIsInitialized = resultCard.getInt("pin_is_init") == 1;
                boolean cardSuspendedStatus = resultCard.getInt("suspended_status") == 1;
                byte[] cardPinHash = fromHexRepr(resultCard.getString("pin_hash"));

                String associatedAccountId = resultCard.getString("account_id");
                if(!associatedAccountId.equals(associatedAccount.getAccountId()))
                    throw new IllegalArgumentException("Real associated account id does not match id of account given as parameter!");

                return new DebitCard(cardSuspendedStatus, cardPinIsInitialized, cardPinHash, empAssistant,
                        cardOwner, cardId, cardName, cardEmissionDate, associatedAccount);
            }

            return null;
        }
    }

    protected String getOwnerId(String cardId) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT owner_id\n" +
                                "FROM card\n" +
                                "WHERE id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, cardId);

            ResultSet resultId = preparedStatement.executeQuery();

            if(resultId.next())
                return resultId.getString("owner_id");

            return null;
        }
    }

    protected String getEmpAssistantId(String cardId) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT emp_assistant_id\n" +
                                "FROM card\n" +
                                "WHERE id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, cardId);

            ResultSet resultId = preparedStatement.executeQuery();

            if(resultId.next())
                return resultId.getString("emp_assistant_id");

            return null;
        }
    }

    protected String getAssociatedAccountId(String cardId) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT account_id\n" +
                                "FROM debit_card\n" +
                                "WHERE id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, cardId);

            ResultSet resultId = preparedStatement.executeQuery();

            if(resultId.next())
                return resultId.getString("account_id");

            return null;
        }
    }
}
