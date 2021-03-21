package Rest;

class AccountIdGenerator {

    public static String BASIC_PREFIX;
    public static int basicCounter;

    public static String CURRENT_PREFIX;
    public static int currentCounter;

    public static String SAVINGS_PREFIX;
    public static int savingsCounter;

    public static String DEPOT_PREFIX;
    public static int depotCounter;

    static{
        BASIC_PREFIX = "ba";
        basicCounter = 19382875;

        CURRENT_PREFIX = "cr";
        currentCounter = 23829424;

        SAVINGS_PREFIX = "sv";
        savingsCounter = 78394728;

        DEPOT_PREFIX = "dp";
        depotCounter = 42749831;
    }

    public static String getId(String type){

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
}
