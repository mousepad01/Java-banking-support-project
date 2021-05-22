package AppManager;

import java.sql.*;
import java.util.ArrayList;
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

    protected ArrayList<Employee> loadAllEmployees() throws SQLException {

        try(Connection db = DbConfig.dbConnection()) {

            String toExecute = "SELECT p.*, e.hire_date, e.job, e.workplace, e.salary\n" +
                                "FROM employee e, person p\n" +
                                "WHERE e.id = p.id;";

            Statement statement = db.createStatement();
            ResultSet resultEmps = statement.executeQuery(toExecute);

            ArrayList<Employee> toReturn = new ArrayList<>();

            while(resultEmps.next()){

                String personId = resultEmps.getString("id");
                String personName = resultEmps.getString("name");
                String personSurname = resultEmps.getString("surname");
                Date personBirthdate = resultEmps.getDate("birthdate");
                String personAddress = resultEmps.getString("address");
                String personEmail = resultEmps.getString("email");
                String personPhone = resultEmps.getString("phone");

                Date employeeHireDate = resultEmps.getDate("hire_date");
                String employeeJob = resultEmps.getString("job");
                String employeeWorkplace = resultEmps.getString("workplace");
                int employeeSalary = resultEmps.getInt("salary");

                toReturn.add(new Employee(personName, personSurname, personBirthdate.toString(), personAddress,
                                            personEmail, personPhone, employeeHireDate.toString(), employeeJob,
                                            employeeWorkplace, personId, employeeSalary));
            }

            return toReturn;
        }
    }

    // incarca fara conturi si carduri asociate
    protected ArrayList<Client> loadAllClients() throws SQLException {

        try(Connection db = DbConfig.dbConnection()) {

            String toExecute = "SELECT p.*, c.registration_date\n" +
                                "FROM person p, client c\n" +
                                "WHERE p.id = c.id;";

            Statement statement = db.createStatement();
            ResultSet resultClients = statement.executeQuery(toExecute);

            ArrayList<Client> toReturn = new ArrayList<>();

            while(resultClients.next()){

                String personId = resultClients.getString("id");
                String personName = resultClients.getString("name");
                String personSurname = resultClients.getString("surname");
                Date personBirthdate = resultClients.getDate("birthdate");
                String personAddress = resultClients.getString("address");
                String personEmail = resultClients.getString("email");
                String personPhone = resultClients.getString("phone");

                Date clientRegistrationDate = resultClients.getDate("registration_date");

                toReturn.add(new Client(personName, personSurname, personBirthdate.toString(), personAddress, personEmail,
                                        personPhone, clientRegistrationDate.toString(), personId, null, null));
            }

            return toReturn;
        }
    }

    protected Employee loadEmployee(String id) throws SQLException {

        try(Connection db = DbConfig.dbConnection()) {

            String toExecute = "SELECT p.*, e.hire_date, e.job, e.workplace, e.salary\n" +
                                "FROM employee e, person p\n" +
                                "WHERE e.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, id);

            ResultSet resultEmp = preparedStatement.executeQuery();

            if(resultEmp.next()){

                String personId = resultEmp.getString("id");
                String personName = resultEmp.getString("name");
                String personSurname = resultEmp.getString("surname");
                Date personBirthdate = resultEmp.getDate("birthdate");
                String personAddress = resultEmp.getString("address");
                String personEmail = resultEmp.getString("email");
                String personPhone = resultEmp.getString("phone");

                Date employeeHireDate = resultEmp.getDate("hire_date");
                String employeeJob = resultEmp.getString("job");
                String employeeWorkplace = resultEmp.getString("workplace");
                int employeeSalary = resultEmp.getInt("salary");

                return new Employee(personName, personSurname, personBirthdate.toString(), personAddress,
                                    personEmail, personPhone, employeeHireDate.toString(), employeeJob,
                                    employeeWorkplace, personId, employeeSalary);
            }

            return null;
        }
    }

    // incarca fara conturi si carduri asociate
    protected Client loadClient(String id) throws SQLException {

        try(Connection db = DbConfig.dbConnection()) {

            String toExecute = "SELECT p.*, c.registration_date\n" +
                                "FROM person p, client c\n" +
                                "WHERE p.id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            ResultSet resultClient = preparedStatement.executeQuery(toExecute);

            if(resultClient.next()){

                String personId = resultClient.getString("id");
                String personName = resultClient.getString("name");
                String personSurname = resultClient.getString("surname");
                Date personBirthdate = resultClient.getDate("birthdate");
                String personAddress = resultClient.getString("address");
                String personEmail = resultClient.getString("email");
                String personPhone = resultClient.getString("phone");

                Date clientRegistrationDate = resultClient.getDate("registration_date");

                return new Client(personName, personSurname, personBirthdate.toString(), personAddress, personEmail,
                                    personPhone, clientRegistrationDate.toString(), personId, null, null);
            }

            return null;
        }
    }
}


