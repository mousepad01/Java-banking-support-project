package AppIO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbInit {

    private void execute(String toExecute){

        try (Connection db = DbConfig.dbConnection()) {

            Statement sqlStatement = db.createStatement();
            sqlStatement.execute(toExecute);
        }
        catch(SQLException err){
            System.out.println("Eroare a bazei de date: " + err.getErrorCode() + ": " + err.getMessage());
        }
    }

    public DbInit() {

        this.execute("CREATE TABLE IF NOT EXISTS person(\n" +
                            "    id VARCHAR(12) PRIMARY KEY,\n" +
                            "    name VARCHAR(200),\n" +
                            "    surname VARCHAR(200),\n" +
                            "    birthdate DATETIME,\n" +
                            "    address VARCHAR(300),\n" +
                            "    email VARCHAR(100),\n" +
                            "    phone VARCHAR(10)\n" +
                            ");");

        this.execute("CREATE TABLE IF NOT EXISTS employee(\n" +
                            "    id VARCHAR(12) PRIMARY KEY,\n" +
                            "    hire_date DATETIME,\n" +
                            "    job VARCHAR(100),\n" +
                            "    workplace VARCHAR(200),\n" +
                            "    salary INT,\n" +
                            "    FOREIGN KEY(id) REFERENCES person(id)\n" +
                            ");");

        this.execute("CREATE TABLE IF NOT EXISTS client(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    registration_date DATETIME,\n" +
                "    FOREIGN KEY(id) REFERENCES person(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS account(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    owner_id VARCHAR(12),\n" +
                "    name VARCHAR(200),\n" +
                "    emp_assistant_id VARCHAR(12),\n" +
                "    creationdate DATETIME,\n" +
                "    balance DOUBLE,\n" +
                "    flags TINYINT,\n" +
                "    FOREIGN KEY(owner_id) REFERENCES client(id),\n" +
                "    FOREIGN KEY(emp_assistant_id) REFERENCES employee(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS depot_account(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    term INT,\n" +
                "    last_updated_term DATETIME,\n" +
                "    FOREIGN KEY(id) REFERENCES account(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS savings_account(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    interest_rate DOUBLE,\n" +
                "    last_updated DATETIME,\n" +
                "    FOREIGN KEY(id) REFERENCES account(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS account_with_card(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    card_id VARCHAR(12),\n" +
                "    FOREIGN KEY(id) REFERENCES account(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS basic_account(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    FOREIGN KEY(id) REFERENCES account_with_card(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS current_account(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    transfer_fee DOUBLE,\n" +
                "    extract_fee DOUBLE,\n" +
                "    add_fee DOUBLE,\n" +
                "    FOREIGN KEY(id) REFERENCES account_with_card(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS card(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    emp_assistant_id VARCHAR(12),\n" +
                "    owner_id VARCHAR(12),\n" +
                "    name VARCHAR(200),\n" +
                "    emission_date DATETIME,\n" +
                "    pin_is_init TINYINT,\n" +
                "    suspended_status TINYINT,\n" +
                "    pin_hash VARCHAR(64),\n" +
                "    FOREIGN KEY(owner_id) REFERENCES client(id),\n" +
                "    FOREIGN KEY(emp_assistant_id) REFERENCES employee(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS debit_card(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    account_id VARCHAR(12),\n" +
                "    FOREIGN KEY(id) REFERENCES card(id),\n" +
                "    FOREIGN KEY(account_id) REFERENCES account_with_card(id)\n" +
                ");");

        this.execute("CREATE TABLE IF NOT EXISTS credit_card(\n" +
                "\tid VARCHAR(12) PRIMARY KEY,\n" +
                "    active_status TINYINT,\n" +
                "    total_amount DOUBLE,\n" +
                "    credit_amount DOUBLE,\n" +
                "    FOREIGN KEY(id) REFERENCES card(id)\n" +
                ");");

        this.execute("ALTER TABLE account_with_card\n" +
                "ADD FOREIGN KEY(card_id) REFERENCES debit_card(id);");

    }
}
