package AppIO;

import AppManager.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public void add(CreditCard toAdd) throws SQLException {

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

    public void add(DebitCard toAdd) throws SQLException {

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
}
