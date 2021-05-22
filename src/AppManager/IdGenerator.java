package AppManager;

import java.sql.SQLException;

public class IdGenerator {

    public static String BASIC_PREFIX;
    public static String CURRENT_PREFIX;
    public static String SAVINGS_PREFIX;
    public static String DEPOT_PREFIX;
    public static String CREDIT_PREFIX;
    public static String DEBIT_PREFIX;
    
    private long personCounter;
    private long basicCounter;
    private long currentCounter;
    private long savingsCounter;
    private long depotCounter;
    private long creditCounter;
    private long debitCounter;

    static{

        BASIC_PREFIX = "ba";
        CURRENT_PREFIX = "cr";
        SAVINGS_PREFIX = "sv";
        DEPOT_PREFIX = "dp";
        CREDIT_PREFIX = "cr";
        DEBIT_PREFIX = "db";

        instance = null;
    }

    private static IdGenerator instance;

    public static IdGenerator getIdGenerator(){

        if(instance == null)
            instance = new IdGenerator();

        return instance;
    }
    
    private IdGenerator(){

        UtilsDb db = new UtilsDb();

        try{

            long[] counters = db.getCounters("main");

            personCounter = counters[0];
            basicCounter = counters[1];
            currentCounter = counters[2];
            savingsCounter = counters[3];
            depotCounter = counters[4];
            creditCounter = counters[5];
            debitCounter = counters[6];
        }
        catch (Exception err){
            throw new RuntimeException("Fatal error (could not load id counters): " + err.getMessage());
        }
    }

    public String getAccountId(String type){

        String toReturn;

        switch (type) {
            case "BASIC" -> {
                basicCounter += 1;
                toReturn = BASIC_PREFIX + basicCounter;
            }
            case "CURRENT" -> {
                currentCounter += 1;
                toReturn = CURRENT_PREFIX + currentCounter;
            }
            case "SAVINGS" -> {
                savingsCounter += 1;
                toReturn = SAVINGS_PREFIX + savingsCounter;
            }
            case "DEPOT" -> {
                depotCounter += 1;
                toReturn = DEPOT_PREFIX + depotCounter;
            }
            default -> throw new RuntimeException("Invalid account type");
        }

        try{

            UtilsDb db = new UtilsDb();
            db.saveCounters("main", this);
        }
        catch (SQLException err){

            Logger log = Logger.getLogger();
            log.logMessage("ERROR: counter ids could not be saved in database: " + err.getMessage());
        }

        return toReturn;
    }

    public String getCardId(String type){

        String toReturn;

        switch (type) {
            case "DEBIT" -> {
                debitCounter += 1;
                toReturn = DEBIT_PREFIX + debitCounter;
            }
            case "CREDIT" -> {
                creditCounter += 1;
                toReturn = CREDIT_PREFIX + creditCounter;
            }
            default -> throw new RuntimeException("Invalid card type");
        }

        try{

            UtilsDb db = new UtilsDb();
            db.saveCounters("main", this);
        }
        catch (SQLException err){

            Logger log = Logger.getLogger();
            log.logMessage("ERROR: counter ids could not be saved in database: " + err.getMessage());
        }

        return toReturn;
    }

    public String getPersonId(){

        personCounter += 1;

        try{

            UtilsDb db = new UtilsDb();
            db.saveCounters("main", this);
        }
        catch (SQLException err){

            Logger log = Logger.getLogger();
            log.logMessage("ERROR: counter ids could not be saved in database: " + err.getMessage());
        }

        return Long.toString(personCounter);
    }

    public long[] previewIds(){
        return new long[]{personCounter, basicCounter, currentCounter, savingsCounter, depotCounter, creditCounter, debitCounter};
    }
}