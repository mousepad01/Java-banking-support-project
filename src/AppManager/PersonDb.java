package AppManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class PersonDb {

    private void add(Person toAdd) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO person (id, name, surname, birthdate, address, email, phone) VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getId());
            preparedStatement.setString(2, toAdd.getName());
            preparedStatement.setString(3, toAdd.getSurname());
            preparedStatement.setDate(4, toAdd.getBirthDate());
            preparedStatement.setString(5, toAdd.getAddress());
            preparedStatement.setString(6, toAdd.getEmail());
            preparedStatement.setString(7, toAdd.getPhoneNumber());

            preparedStatement.execute();
        }
    }

    protected void add(Employee toAdd) throws SQLException {

        this.add((Person) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO employee (id, hire_date, job, workplace, salary) VALUES(?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getId());
            preparedStatement.setDate(2, toAdd.getHireDate());
            preparedStatement.setString(3, toAdd.getJob());
            preparedStatement.setString(4, toAdd.getWorkplace());
            preparedStatement.setInt(5, toAdd.getSalary());

            preparedStatement.execute();
        }
    }

    protected void add(Client toAdd) throws SQLException {

        this.add((Person) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO client (id, registration_date) VALUES(?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getId());
            preparedStatement.setDate(2, toAdd.getRegistrationDate());

            preparedStatement.execute();

            AccountDb accountDb = new AccountDb();

            Iterator accIter = toAdd.getAllAccounts();
            while(accIter.hasNext()){

                Map.Entry entry = (Map.Entry) accIter.next();
                Account acc = (Account) entry.getValue();

                if(acc instanceof DepotAccount)
                    accountDb.add((DepotAccount) acc);

                else if(acc instanceof SavingsAccount)
                    accountDb.add((SavingsAccount) acc);

                else if(acc instanceof BasicAccount)
                    accountDb.add((BasicAccount) acc);

                else if(acc instanceof CurrentAccount)
                    accountDb.add((CurrentAccount) acc);
            }

            CardDb cardDb = new CardDb();

            Iterator cardIter = toAdd.getAllCards();
            while(cardIter.hasNext()){

                Map.Entry entry = (Map.Entry) cardIter.next();
                Card acc = (Card) entry.getValue();

                if(acc instanceof CreditCard)
                    cardDb.add((CreditCard) acc);

                else if(acc instanceof DebitCard)
                    cardDb.add((DebitCard) acc);
            }

            accIter = toAdd.getAllAccounts();
            while(accIter.hasNext()){

                Map.Entry entry = (Map.Entry) accIter.next();
                Account acc = (Account) entry.getValue();

                if(acc instanceof AccountWithCard)
                    accountDb.linkWithCard((AccountWithCard) acc);
            }

        }

    }

    protected void update(Person toUpdate) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE person \n" +
                                "SET address = ?, email = ?, phone = ?" +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toUpdate.getAddress());
            preparedStatement.setString(2, toUpdate.getEmail());
            preparedStatement.setString(3, toUpdate.getPhoneNumber());
            preparedStatement.setString(4, toUpdate.getId());

            preparedStatement.execute();
        }
    }

    protected void update(Employee toUpdate) throws SQLException {

        this.update((Person) toUpdate);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "UPDATE employee \n" +
                                "SET job = ?, workplace = ?, salary = ?" +
                                "WHERE id = ? \n";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, toUpdate.getJob());
            preparedStatement.setString(2, toUpdate.getWorkplace());
            preparedStatement.setInt(3, toUpdate.getSalary());
            preparedStatement.setString(4, toUpdate.getId());

            preparedStatement.execute();
        }
    }

    protected void update(Client toUpdate) throws SQLException {

        this.update((Person) toUpdate);

        // aceasta metoda updateaza doar datele personale ale clientului,
        // stergerea sau modificarea conturilor asociate are loc separat
    }
}


