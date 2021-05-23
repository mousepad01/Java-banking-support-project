package AppManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

// singleton dar care nu returneaza decat o singura data referinta
// pentru a nu putea accesa db manager din nici un alt loc
// decat atunci cand creez thread ul dorit la inceputul rularii programului

public class DbManager implements Runnable{

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

    private static final HashMap<Long, DbManager> threadDbManagers;

    public static DbManager getDbManger(Long threadId){
        return DbManager.threadDbManagers.get(threadId);
    }

    static{
        threadDbManagers = new HashMap<>();
    }

    private boolean done;
    private final LinkedList<ChangeListElement> changeList;
    private final Semaphore changeListCounter;

    private final PersonDb personDb;
    private final AccountDb accountDb;
    private final CardDb cardDb;

    public void maskAsDone(){
        this.done = true;
    }

    public void setChange(DbObject dbObject, String objectDbId, int statusCode){
        synchronized (this.changeList){
            this.changeList.addLast(new ChangeListElement(dbObject, objectDbId, statusCode));
        }
    }

    public void processChange(ChangeListElement toProcess){

    }

    private ChangeListElement getChange(){
        synchronized(this.changeList){
            return this.changeList.remove();
        }
    }

    public DbManager(Semaphore changeListCounter){

        this.done = false;
        this.changeList = new LinkedList<>();
        this.changeListCounter = changeListCounter;

        this.personDb = new PersonDb();
        this.accountDb = new AccountDb();
        this.cardDb = new CardDb();

        threadDbManagers.put(Thread.currentThread().getId(), this);
    }

    public void run(){

        try{
            while(!this.done)
                while(this.changeListCounter.availablePermits() > 0){

                    this.changeListCounter.acquire();
                    this.processChange(this.getChange());
                }
        }
        catch (Exception err) {

            Logger log = Logger.getLogger();
            log.logMessage("Error while running main loop of a DbManager thread: " + err.getMessage());
        }
    }
}
