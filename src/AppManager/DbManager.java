package AppManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

// singleton dar care nu returneaza decat o singura data referinta
// pentru a nu putea accesa db manager din nici un alt loc
// decat atunci cand creez thread ul dorit la inceputul rularii programului

public class DbManager implements Runnable{

    private static boolean alreadyCreated;
    public static boolean done;

    public static DbManager CreateDbManager(int timerSeconds){

        if(!alreadyCreated){

            alreadyCreated = true;
            return new DbManager(timerSeconds);
        }

        return null;
    }

    public static boolean isAlreadyCreated(){
        return alreadyCreated;
    }

    //----------

    private final int timerSeconds;

    private static class ChangeListElement {

        private Class<?> objectType;
        private String objectDbId;

        // statusCode:
        // 1 - update
        // 2 - new object
        // 3 - delete

        private int statusCode;

        public ChangeListElement(DbObject dbObject, String objectDbId, int statusCode){

            if(dbObject instanceof Employee){

                this.objectType = Employee.class;

                if(statusCode == 1 || statusCode == 2)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof Client){

                this.objectType = Client.class;

                if(statusCode == 1 || statusCode == 2)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof BasicAccount){

                this.objectType = BasicAccount.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof CurrentAccount){

                this.objectType = CurrentAccount.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof DepotAccount){

                this.objectType = DepotAccount.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof SavingsAccount){

                this.objectType = SavingsAccount.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof CreditCard){

                this.objectType = CreditCard.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }

            else if(dbObject instanceof DebitCard){

                this.objectType = DebitCard.class;

                if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                    this.statusCode = statusCode;
                else
                    throw new IllegalArgumentException("Wrong status type");

                this.objectDbId = objectDbId;
            }
        }

        public Class<?> getObjectType(){
            return objectType;
        }

        public String getObjectDbId(){
            return objectDbId;
        }

        public int getStatusCode(){
            return statusCode;
        }
    }

    private LinkedList<ChangeListElement> changeList;

    synchronized protected void setChenge(DbObject dbObject, String objectDbId, int statusCode){

        changeList.addLast(new ChangeListElement(dbObject, objectDbId, statusCode));
    }

    protected DbManager(int timerSeconds){

        this.timerSeconds = timerSeconds;
    }

    public void run(){

        changeList = new LinkedList<>();

        PersonDb personDb = new PersonDb();
        AccountDb accountDb = new AccountDb();
        CardDb cardDb = new CardDb();

        /*try {

            ArrayList<Employee> loadedEmployees = personDb.loadAllEmployees();
            ArrayList<Client> loadedClients = personDb.loadAllClients();


        } catch (SQLException err) {

            Logger log = Logger.getLogger();
            log.logMessage("Error while trying to load initial info from database: " + err.getMessage());
            return;
        }*/

        while(!DbManager.done){



            try {
                Thread.sleep(1000 * this.timerSeconds);
            }
            catch (InterruptedException err) {

                Logger log = Logger.getLogger();
                log.logMessage("Error while sleeping in main loop in db manager thread: " + err.getMessage());
                return;
            }
        }
    }
}
