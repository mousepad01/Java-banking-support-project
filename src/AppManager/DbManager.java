package AppManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class DbManager implements Runnable{

    private static class ChangeListElement {

        // statusCode:
        // 1 - update
        // 2 - new object
        // 3 - delete
        // 4 - load from database

        public int statusCode;
        public DbObject dbObject;
        public String objectDbId;
        public Class<?> objectType;
        public DbObject[] loadDependencies;

        // pentru update, new, delete
        public ChangeListElement(DbObject dbObject, int statusCode){

            if(dbObject != null){

                if(dbObject instanceof Employee){

                    if(statusCode == 1 || statusCode == 2)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof Client){

                    if(statusCode == 1 || statusCode == 2)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof BasicAccount){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof CurrentAccount){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof DepotAccount){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof SavingsAccount){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof CreditCard){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else if(dbObject instanceof DebitCard){

                    if(statusCode == 1 || statusCode == 2 || statusCode == 3)
                        this.statusCode = statusCode;
                    else
                        throw new IllegalArgumentException("Wrong status type");
                }

                else
                    throw new IllegalArgumentException("Object to change is not appropriate: class " + dbObject.getClass());

                this.dbObject = dbObject;
            }

            else
                throw new NullPointerException("No object to mark changes to");
        }

        // pentru load
        public ChangeListElement(String objectDbId, Class<?> objectType, DbObject[] loadDependencies){

            if(objectDbId == null)
                throw new NullPointerException("No ID to be searched in database");

            if(objectType != Employee.class && objectType != Client.class && objectType != DepotAccount.class &&
                objectType != SavingsAccount.class && objectType != BasicAccount.class &&
                objectType != CurrentAccount.class && objectType != CreditCard.class && objectType != DebitCard.class)

                throw new IllegalArgumentException("Invalid object class to be searched in database");

            this.objectDbId = objectDbId;
            this.objectType = objectType;
            this.statusCode = 4;

            // loadDependencies vor fi procesate conform unei conventii:
            // by default:
            // in cazul clientului / angajatului / id generator, dependentele sunt ignorate
            //
            // in cazul conturilor, primul angajat este considerat emp assistant, primul client - owner ul
            //                      restul sunt ignorate
            //
            // in cazul cardurilor, primul angajat este considerat emp assistant, primul client - owner ul,
            //                      daca este card de debit: primul cont care are card - contul asociat
            //                      restul sunt ignorate

            this.loadDependencies = loadDependencies;
        }

        public ChangeListElement(END_DB_CONN end){
            this.dbObject = end;
        }
    }

    private static class END_DB_CONN extends DbObject { }

    // pentru a avea un obiect pe care sa fac wait si notify
    // daca foloseam direct referinte in map ul loadedObjects, nu ar mai fi functionat
    private static class LoadRef {
        public DbObject ref;
    }

    // (thread, thread db manager corespunzator) -> db manager corespunzator
    public static final HashMap<Pair, DbManager> threadDbManagers;
    private static final HashMap<String, LoadRef> loadedObjects;

    public static DbManager getDbManger(Long currentThreadId, Long threadId){
        return DbManager.threadDbManagers.get(new Pair(currentThreadId, threadId));
    }

    static{
        threadDbManagers = new HashMap<>();
        loadedObjects = new HashMap<>();
    }

    private final LinkedList<ChangeListElement> changeList;
    private final Semaphore changeListCounter;

    private final PersonDb personDb;
    private final AccountDb accountDb;
    private final CardDb cardDb;

    private final Logger log;

    public void maskAsDone(){
        synchronized (this.changeList){
            this.changeList.add(new ChangeListElement(new END_DB_CONN()));
            this.changeListCounter.release();
        }
    }

    // pentru update, new, delete
    protected void setChange(DbObject dbObject, int statusCode){
        synchronized (this.changeList){
            this.changeList.addLast(new ChangeListElement(dbObject, statusCode));
            this.changeListCounter.release();
        }
    }

    synchronized private LoadRef loadRefInit(String key){

        if(loadedObjects.get(key) == null)
            loadedObjects.put(key, new LoadRef());

        return loadedObjects.get(key);
    }

    // pentru load
    protected DbObject loadObject(String objectDbId, Class<?> objectType, DbObject[] loadDependencies) throws InterruptedException {

        LoadRef loadRef = loadRefInit(objectDbId);
        synchronized(loadRef){

            if(loadRef.ref == null){

                synchronized (this.changeList){
                    this.changeList.addLast(new ChangeListElement(objectDbId, objectType, loadDependencies));
                }

                loadRef.wait();
            }
        }

        return loadRef.ref;
    }

    private boolean processChange(ChangeListElement toProcess) throws SQLException {

        if(toProcess.dbObject instanceof Employee employee){

            if(toProcess.statusCode == 1)
                this.personDb.update(employee);

            else if(toProcess.statusCode == 2)
                this.personDb.add(employee);
        }

        else if(toProcess.dbObject instanceof Client client){

            if(toProcess.statusCode == 1)
                this.personDb.update(client);

            else if(toProcess.statusCode == 2)
                this.personDb.add(client);
        }

        else if(toProcess.dbObject instanceof BasicAccount basicAccount){

            if(toProcess.statusCode == 1)
                this.accountDb.update(basicAccount);

            else if(toProcess.statusCode == 2)
                this.accountDb.add(basicAccount);

            else if(toProcess.statusCode == 3)
                this.accountDb.delete(basicAccount);
        }

        else if(toProcess.dbObject instanceof CurrentAccount currentAccount){

            if(toProcess.statusCode == 1)
                this.accountDb.update(currentAccount);

            else if(toProcess.statusCode == 2)
                this.accountDb.add(currentAccount);

            else if(toProcess.statusCode == 3)
                this.accountDb.delete(currentAccount);
        }

        else if(toProcess.dbObject instanceof DepotAccount depotAccount){

            if(toProcess.statusCode == 1)
                this.accountDb.update(depotAccount);

            else if(toProcess.statusCode == 2)
                this.accountDb.add(depotAccount);

            else if(toProcess.statusCode == 3)
                this.accountDb.delete(depotAccount);
        }

        else if(toProcess.dbObject instanceof SavingsAccount savingsAccount){

            if(toProcess.statusCode == 1)
                this.accountDb.update(savingsAccount);

            else if(toProcess.statusCode == 2)
                this.accountDb.add(savingsAccount);

            else if(toProcess.statusCode == 3)
                this.accountDb.delete(savingsAccount);
        }

        else if(toProcess.dbObject instanceof CreditCard creditCard){

            if(toProcess.statusCode == 1)
                this.cardDb.update(creditCard);

            else if(toProcess.statusCode == 2)
                this.cardDb.add(creditCard);

            else if(toProcess.statusCode == 3)
                this.cardDb.delete(creditCard);
        }

        else if(toProcess.dbObject instanceof DebitCard debitCard){

            if(toProcess.statusCode == 1)
                this.cardDb.update(debitCard);

            else if(toProcess.statusCode == 2)
                this.cardDb.add(debitCard);

            else if(toProcess.statusCode == 3)
                this.cardDb.delete(debitCard);
        }

        else if(toProcess.statusCode == 4){

            DbObject loaded = null;

            if(toProcess.objectType == Employee.class)
                loaded = this.personDb.loadEmployee(toProcess.objectDbId);

            else if(toProcess.objectType == Client.class)
                loaded = this.personDb.loadClient(toProcess.objectDbId);

            else if(toProcess.objectType == SavingsAccount.class || toProcess.objectType == DepotAccount.class ||
                    toProcess.objectType == BasicAccount.class || toProcess.objectType == CurrentAccount.class ||
                    toProcess.objectType == CreditCard.class){

                Employee empAssistant = null;
                Client owner = null;

                boolean empFound = false;
                boolean ownerFound = false;

                for(int i = 0; i < toProcess.loadDependencies.length; i++){

                    if(!empFound && toProcess.loadDependencies[i] instanceof Employee) {
                        empAssistant = (Employee) toProcess.loadDependencies[i];
                        empFound = true;
                    }

                    if(!ownerFound && toProcess.loadDependencies[i] instanceof Client) {
                        owner = (Client) toProcess.loadDependencies[i];
                        ownerFound = true;
                    }
                }

                if(!empFound || !ownerFound) {

                    LoadRef loadRef = loadedObjects.get(toProcess.objectDbId);
                    synchronized (loadRef){
                        loadRef.notifyAll();
                    }

                    throw new IllegalArgumentException("Invalid dependencies while trying to load an account or card from DB");
                }

                if(toProcess.objectType == SavingsAccount.class)
                    loaded = this.accountDb.loadSavingsAccount(toProcess.objectDbId, owner, empAssistant);

                else if (toProcess.objectType == DepotAccount.class)
                    loaded = this.accountDb.loadDepotAccount(toProcess.objectDbId, owner, empAssistant);

                else if (toProcess.objectType == BasicAccount.class)
                    loaded = this.accountDb.loadBasicAccount(toProcess.objectDbId, owner, empAssistant);

                else if (toProcess.objectType == CurrentAccount.class)
                    loaded = this.accountDb.loadCurrentAccount(toProcess.objectDbId, owner, empAssistant);

                else if (toProcess.objectType == CreditCard.class)
                    loaded = this.cardDb.loadCreditCard(toProcess.objectDbId, owner, empAssistant);
            }

            else if(toProcess.objectType == DebitCard.class){

                Employee empAssistant = null;
                Client owner = null;
                AccountWithCard account = null;

                boolean empFound = false;
                boolean ownerFound = false;
                boolean accoundFound = false;

                for(int i = 0; i < toProcess.loadDependencies.length; i++){

                    if(!empFound && toProcess.loadDependencies[i] instanceof Employee) {
                        empAssistant = (Employee) toProcess.loadDependencies[i];
                        empFound = true;
                    }

                    if(!ownerFound && toProcess.loadDependencies[i] instanceof Client) {
                        owner = (Client) toProcess.loadDependencies[i];
                        ownerFound = true;
                    }

                    if(!accoundFound && toProcess.loadDependencies[i] instanceof AccountWithCard) {
                        account = (AccountWithCard) toProcess.loadDependencies[i];
                        accoundFound = true;
                    }
                }

                if(!empFound || !ownerFound || !accoundFound){

                    LoadRef loadRef = loadedObjects.get(toProcess.objectDbId);
                    synchronized (loadRef){
                        loadRef.notifyAll();
                    }

                    throw new IllegalArgumentException("Invalid dependencies while trying to load card from DB");
                }

                loaded = this.cardDb.loadDebitCard(toProcess.objectDbId, owner, empAssistant, account);
            }

            LoadRef loadRef = loadedObjects.get(toProcess.objectDbId);
            synchronized (loadRef){

                loadRef.ref = loaded;
                loadRef.notifyAll();
            }
        }

        return toProcess.dbObject instanceof END_DB_CONN;
    }

    private ChangeListElement getChange(){
        synchronized(this.changeList){
            return this.changeList.remove();
        }
    }

    public DbManager(Semaphore changeListCounter){

        this.changeList = new LinkedList<>();
        this.changeListCounter = changeListCounter;

        this.personDb = new PersonDb();
        this.accountDb = new AccountDb();
        this.cardDb = new CardDb();

        this.log = Logger.getLogger();
    }

    @Override
    public void run(){

        boolean done = false;

        try{
            while(!done){

                this.changeListCounter.acquire();
                done = this.processChange(this.getChange());
            }
        }
        catch (Exception err) {
            this.log.logMessage("Error while running main loop of DbManager thread " +
                                Thread.currentThread().getId() + "(DbManager thread closed): " + err.getMessage());
        }
    }
}
