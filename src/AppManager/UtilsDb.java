package AppManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilsDb {
    
    private void saveCounters(String cntSessionId, long[] toSave) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "INSERT INTO id_counters(counters_session_id, person_ct, basic_ct, " +
                                                        "current_ct, savings_ct, depot_ct, debit_ct, credit_ct)" +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setString(1, cntSessionId);
            preparedStatement.setLong(2, toSave[0]);
            preparedStatement.setLong(3, toSave[1]);
            preparedStatement.setLong(4, toSave[2]);
            preparedStatement.setLong(5, toSave[3]);
            preparedStatement.setLong(6, toSave[4]);
            preparedStatement.setLong(7, toSave[5]);
            preparedStatement.setLong(8, toSave[6]);

            preparedStatement.execute();
        }
    }

    protected void updateCounters(String cntSessionId, IdGenerator idgen) throws SQLException {

        try(Connection db = DbConfig.dbConnection()){

            long[] counters = idgen.previewIds();
            String toExecute = "UPDATE id_counters\n" +
                                "SET person_ct = ?, basic_ct = ?, current_ct = ?, savings_ct = ?, " +
                                "depot_ct = ?, credit_ct = ?, debit_ct = ?\n" +
                                "WHERE counters_session_id = ?;";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);

            preparedStatement.setLong(1, counters[0]);
            preparedStatement.setLong(2, counters[1]);
            preparedStatement.setLong(3, counters[2]);
            preparedStatement.setLong(4, counters[3]);
            preparedStatement.setLong(5, counters[4]);
            preparedStatement.setLong(6, counters[5]);
            preparedStatement.setLong(7, counters[6]);
            preparedStatement.setString(8, cntSessionId);

            preparedStatement.execute();
        }
    }

    protected long[] getCounters(String cntSessionId) throws SQLException{

        try(Connection db = DbConfig.dbConnection()){

            String toExecute = "SELECT * " +
                                "FROM id_counters " +
                                "WHERE counters_session_id = ?";

            PreparedStatement preparedStatement = db.prepareStatement(toExecute);
            preparedStatement.setString(1, cntSessionId);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next())
                return new long[]{result.getLong(2), result.getLong(3), result.getLong(4),
                                    result.getLong(5), result.getLong(6),
                                    result.getLong(7), result.getLong(8)};
            else {

                long[] newCounters = new long[]{192837492020L, 19382875, 23829424, 78394728, 42749831, 62736288, 82739103};
                this.saveCounters(cntSessionId, newCounters);

                return newCounters;
            }
        }
    }
}
