package ModelManager;

class IdGenerator {

    public static String BASIC_PREFIX;
    protected static int basicCounter;

    public static String CURRENT_PREFIX;
    protected static int currentCounter;

    public static String SAVINGS_PREFIX;
    protected static int savingsCounter;

    public static String DEPOT_PREFIX;
    protected static int depotCounter;

    public static String CREDIT_PREFIX;
    protected static int creditCounter;

    public static String DEBIT_PREFIX;
    protected static int debitCounter;

    protected static long personCounter;

    static{
        BASIC_PREFIX = "ba";
        basicCounter = 19382875;

        CURRENT_PREFIX = "cr";
        currentCounter = 23829424;

        SAVINGS_PREFIX = "sv";
        savingsCounter = 78394728;

        DEPOT_PREFIX = "dp";
        depotCounter = 42749831;

        CREDIT_PREFIX = "cr";
        creditCounter = 62736288;

        DEBIT_PREFIX = "db";
        debitCounter = 82739103;

        personCounter = 192837492019L;
    }

    public static String getAccountId(String type){

        switch(type){

            case "BASIC":
                basicCounter += 1;
                return BASIC_PREFIX + basicCounter;

            case "CURRENT":
                currentCounter += 1;
                return CURRENT_PREFIX + currentCounter;

            case "SAVINGS":
                savingsCounter += 1;
                return SAVINGS_PREFIX + savingsCounter;

            case "DEPOT":
                depotCounter += 1;
                return DEPOT_PREFIX + depotCounter;

            default:
                return ""; /// EXCEPTIE
        }
    }

    public static String getCardId(String type){

        switch(type){

            case "DEBIT":
                debitCounter += 1;
                return DEBIT_PREFIX + debitCounter;

            case "CREDIT":
                creditCounter += 1;
                return CREDIT_PREFIX + creditCounter;

            default:
                return ""; // EXCEPTIE
        }
    }

    public static String getPersonId(){

        personCounter += 1;
        return "" + personCounter;
    }
}
