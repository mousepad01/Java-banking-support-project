package AppIO;

import AppManager.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AccountDb {

    private void add(Account toAdd){

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO account (id, owner_id, name, emp_assistant_id, creationdate, balance, flags) VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setString(2, toAdd.getOwner().getId());
            preparedStatement.setString(2, toAdd.getOwner().getName());
            preparedStatement.setString(3, toAdd.getContractAssistant().getId());
            preparedStatement.setDate(4, toAdd.getCreationDate());
            preparedStatement.setDouble(5, toAdd.getBalance());
            preparedStatement.setInt(6, toAdd.getFlags());

            preparedStatement.execute();
        }
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }
    }

    public void add(DepotAccount toAdd){

        this.add((Account) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO depot_account (id, term, last_updated_term) VALUES(?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setInt(2, toAdd.getTerm());
            preparedStatement.setDate(2, toAdd.getLastUpdatedTerm());

            preparedStatement.execute();
        }
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }

    }

    public void add(SavingsAccount toAdd){

        this.add((Account) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO depot_account (id, interest_rate, last_updated) VALUES(?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getAccountId());
            preparedStatement.setDouble(2, toAdd.getInterestRate());
            preparedStatement.setDate(2, toAdd.getLastUpdated());

            preparedStatement.execute();
        }
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }

    }
}
