package AppIO;

import AppManager.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


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

    public void add(DepotAccount toAdd) throws SQLException {

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

    public void add(SavingsAccount toAdd) throws SQLException {

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

    public void linkWithCard(AccountWithCard toUpdate) throws SQLException {

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

    public void add(BasicAccount toAdd) throws SQLException {

        this.add((AccountWithCard) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO basic_account(id) VALUES(?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toAdd.getAccountId());

            preparedStatement.execute();
        }
    }

    public void add(CurrentAccount toAdd) throws SQLException {

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
}
