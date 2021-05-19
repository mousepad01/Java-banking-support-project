package AppIO;

import AppManager.Account;
import AppManager.Person;
import AppManager.Employee;
import AppManager.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersonDb {

    private void add(Person toAdd){

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
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }
    }

    public void add(Employee toAdd){

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
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }
    }

    /*public void add(Client toAdd){

        this.add((Person) toAdd);

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO client (id, registration_date) VALUES(?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, toAdd.getId());
            preparedStatement.setDate(2, toAdd.getRegistrationDate());

            preparedStatement.execute();

            // TODO: adaugare si conturi si carduri

        }
        catch(SQLException err){
            throw new RuntimeException(err.getMessage());
        }
    }*/
}
