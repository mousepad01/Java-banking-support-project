package AppManager;

import java.sql.*;


public class AccountDb {

    private void add(Account toAdd) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO account (id, owner_id, name, emp_assistant_id, creation_date, balance, flags) VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setString(2, toAdd.getOwner().getId());
            preparedStatement.setString(3, toAdd.getName());
            preparedStatement.setString(4, toAdd.getContractAssistant().getId());
            preparedStatement.setDate(5, toAdd.getCreationDate());
            preparedStatement.setDouble(6, toAdd.getBalance());
            preparedStatement.setInt(7, toAdd.getFlags());

            preparedStatement.execute();
        }

    }

    protected void add(DepotAccount toAdd) throws SQLException {

        this.add((Account) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO depot_account (id, term, last_updated_term) VALUES(?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setInt(2, toAdd.getTerm());
            preparedStatement.setDate(3, toAdd.getLastUpdatedTerm());

            preparedStatement.execute();
        }
    }

    protected void add(SavingsAccount toAdd) throws SQLException {

        this.add((Account) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO savings_account (id, interest_rate, last_updated) VALUES(?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setDouble(2, toAdd.getInterestRate());
            preparedStatement.setDate(3, toAdd.getLastUpdated());

            preparedStatement.execute();
        }

    }

    protected void linkWithCard(AccountWithCard toUpdate) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE account_with_card\n" +
                                "SET card_id = ? \n" +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            DebitCard associatedCard = toUpdate.getAssociatedCard();
            preparedStatement.setString(1, associatedCard == null ? null : associatedCard.getCardId());
            preparedStatement.setString(2, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    private void add(AccountWithCard toAdd) throws SQLException {

        this.add((Account) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO account_with_card(id) VALUES(?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());

            preparedStatement.execute();

            try{
                linkWithCard(toAdd);
            }
            catch(SQLException ignored){ }

        }
    }

    protected void add(BasicAccount toAdd) throws SQLException {

        this.add((AccountWithCard) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO basic_account(id) VALUES(?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getAccountId());

            preparedStatement.execute();
        }
    }

    protected void add(CurrentAccount toAdd) throws SQLException {

        this.add((AccountWithCard) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO current_account(id, transfer_fee, extract_fee, add_fee) VALUES(?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setDouble(2, toAdd.getTransactionFee());
            preparedStatement.setDouble(3, toAdd.getExtractFee());
            preparedStatement.setDouble(4, toAdd.getAddFee());

            preparedStatement.execute();
        }
    }

    private void update(Account toUpdate) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE account \n" +
                                "SET balance = ?, flags = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setDouble(1, toUpdate.getBalance());
            preparedStatement.setInt(2, toUpdate.getFlags());
            preparedStatement.setString(3, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    protected void update(DepotAccount toUpdate) throws SQLException {

        this.update((Account) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE depot_account \n" +
                                "SET last_updated_term = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setDate(1, toUpdate.getLastUpdatedTerm());
            preparedStatement.setString(2, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    protected void update(SavingsAccount toUpdate) throws SQLException {

        this.update((Account) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE savings_account \n" +
                                "SET interest_rate = ?, last_updated = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setDouble(1, toUpdate.getInterestRate());
            preparedStatement.setDate(2, toUpdate.getLastUpdated());
            preparedStatement.setString(3, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    private void update(AccountWithCard toUpdate) throws SQLException {

        this.update((Account) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE account_with_card \n" +
                                "SET card_id = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            DebitCard associatedCard = toUpdate.getAssociatedCard();
            preparedStatement.setString(1, associatedCard == null ? null : associatedCard.getCardId());
            preparedStatement.setString(2, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    protected void update(BasicAccount toUpdate) throws SQLException {

        this.update((AccountWithCard) toUpdate);
    }

    protected void update(CurrentAccount toUpdate) throws SQLException {

        this.update((AccountWithCard) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE current_account \n" +
                                "SET transaction_fee = ?, extract_fee = ?, add_fee = ? " +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setDouble(1, toUpdate.getTransactionFee());
            preparedStatement.setDouble(2, toUpdate.getExtractFee());
            preparedStatement.setDouble(3, toUpdate.getAddFee());
            preparedStatement.setString(4, toUpdate.getAccountId());

            preparedStatement.execute();
        }
    }

    private void delete(Account toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM account \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }
    }

    protected void delete(DepotAccount toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM depot_account \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }

        this.delete((Account) toDelete);
    }

    protected void delete(SavingsAccount toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM savings_account \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }

        this.delete((Account) toDelete);
    }

    private void delete(AccountWithCard toDelete) throws SQLException {

        DebitCard associatedCard = toDelete.getAssociatedCard();
        if(associatedCard != null)
            (new CardDb()).delete(associatedCard);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM account_with_card \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }

        this.delete((Account) toDelete);
    }

    protected void delete(BasicAccount toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM basic_account \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }

        this.delete((AccountWithCard) toDelete);
    }

    protected void delete(CurrentAccount toDelete) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "DELETE FROM current_account \n" +
                                "WHERE id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toDelete.getAccountId());

            preparedStatement.execute();
        }

        this.delete((AccountWithCard) toDelete);
    }

    public DepotAccount loadDepotAccount(String id, Client accountOwner, Employee empAssistant) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT a.*, d.term, d.last_updated_term\n" +
                                "FROM account a, depot_account d\n" +
                                "WHERE a.id = d.id\n" +
                                "AND d.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultAccount = preparedStatement.executeQuery();

            if(resultAccount.next()){

                String accountId = resultAccount.getString("id");

                String accountOwnerId = resultAccount.getString("owner_id");
                if(!accountOwnerId.equals(accountOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String accountName = resultAccount.getString("name");

                String empAssistantId = resultAccount.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                Date accountCreationDate = resultAccount.getDate("creation_date");
                double accountBalance = resultAccount.getDouble("balance");
                byte accountFlags = resultAccount.getByte("flags");

                int accountTerm = resultAccount.getInt("term");
                Date accountLastUpdatedTerm = resultAccount.getDate("last_updated_term");

                return new DepotAccount(accountId, accountOwner, accountName, empAssistant, accountCreationDate,
                                        accountBalance, accountFlags, accountTerm, accountLastUpdatedTerm);
            }

            return null;
        }
    }

    public SavingsAccount loadSavingsAccount(String id, Client accountOwner, Employee empAssistant) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT a.*, s.interest_rate, s.last_updated\n" +
                                "FROM account a, savings_account s\n" +
                                "WHERE a.id = s.id\n" +
                                "AND s.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultAccount = preparedStatement.executeQuery();

            if(resultAccount.next()){

                String accountId = resultAccount.getString("id");

                String accountOwnerId = resultAccount.getString("owner_id");
                if(!accountOwnerId.equals(accountOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String accountName = resultAccount.getString("name");

                String empAssistantId = resultAccount.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                Date accountCreationDate = resultAccount.getDate("creation_date");
                double accountBalance = resultAccount.getDouble("balance");
                byte accountFlags = resultAccount.getByte("flags");

                double accountInterestRate = resultAccount.getInt("interest_rate");
                Date accountLastUpdated = resultAccount.getDate("last_updated");

                return new SavingsAccount(accountId, accountOwner, accountName, empAssistant, accountCreationDate,
                                            accountBalance, accountFlags, accountInterestRate, accountLastUpdated);
            }

            return null;
        }
    }

    // nu incarca si eventualul card asociat
    public BasicAccount loadBasicAccount(String id, Client accountOwner, Employee empAssistant) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT a.*\n" +
                                "FROM account a, basic_account b\n" +
                                "WHERE a.id = b.id\n" +
                                "AND b.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultAccount = preparedStatement.executeQuery();

            if(resultAccount.next()){

                String accountId = resultAccount.getString("id");

                String accountOwnerId = resultAccount.getString("owner_id");
                if(!accountOwnerId.equals(accountOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String accountName = resultAccount.getString("name");

                String empAssistantId = resultAccount.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                Date accountCreationDate = resultAccount.getDate("creation_date");
                double accountBalance = resultAccount.getDouble("balance");
                byte accountFlags = resultAccount.getByte("flags");

                return new BasicAccount(accountId, accountOwner, accountName, empAssistant, accountCreationDate,
                                        accountBalance, accountFlags, null);
            }

            return null;
        }
    }

    // nu incarca si eventualul card asociat
    public CurrentAccount loadCurrentAccount(String id, Client accountOwner, Employee empAssistant) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT a.*, c.transaction_fee, c.extract_fee, c.add_fee\n" +
                                "FROM account a, current_account c\n" +
                                "WHERE a.id = c.id\n" +
                                "AND c.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, id);

            ResultSet resultAccount = preparedStatement.executeQuery();

            if(resultAccount.next()){

                String accountId = resultAccount.getString("id");

                String accountOwnerId = resultAccount.getString("owner_id");
                if(!accountOwnerId.equals(accountOwner.getId()))
                    throw new IllegalArgumentException("Real owner id does not match id of client given as parameter!");

                String accountName = resultAccount.getString("name");

                String empAssistantId = resultAccount.getString("emp_assistant_id");
                if(!empAssistantId.equals(empAssistant.getId()))
                    throw new IllegalArgumentException("Real employee assistant id does not match id of client given as parameter!");

                Date accountCreationDate = resultAccount.getDate("creation_date");
                double accountBalance = resultAccount.getDouble("balance");
                byte accountFlags = resultAccount.getByte("flags");

                double accountTransactionFee = resultAccount.getDouble("transaction_fee");
                double accountExtractFee = resultAccount.getDouble("extract_fee");
                double accountAddFee = resultAccount.getDouble("add_fee");

                return new CurrentAccount(accountId, accountOwner, accountName, empAssistant, accountCreationDate,
                                            accountBalance, accountFlags, null, accountTransactionFee,
                                            accountExtractFee, accountAddFee);
            }

            return null;
        }
    }
}
