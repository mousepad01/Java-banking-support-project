package AppManager;

import com.mysql.cj.log.Log;

import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;

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

    private int timerSeconds;

    private HashMap<String, Integer> changedPersons;
    private HashMap<String, Integer> changedAccounts;
    private HashMap<String, Integer> changedCards;

    // changed persons code:
    // 0 - nici o schimbare
    // 1 - pentru update date ale persoanei / angajatului / clientului (fara carduri / conturi !!!)
    // 2 - pentru o persoana nou adaugata

    // changed accounts code:
    // 0 - nici o modificare
    // 1 - pentru update date ale contului
    // 2 - pentru un cont nou creat
    // 3 - pentru un cont sters - va fi eliminat si din hashmap o data ce este procesat

    // changed cards code:
    // 0 - nici o modificare
    // 1 - pentru update a cardului
    // 2 - card nou adaugat
    // 3 - pentru un card sters - va fi eliminat si din hashmap o data procesat

    public void setUpdatePerson(String id){


    }

    public void setUpdateAccount(String id){


    }

    public void setUpdateCard(String id){


    }

    private DbManager(int timerSeconds){

        this.timerSeconds = timerSeconds;
    }

    public void run(){

        changedPersons = new HashMap<>();
        changedAccounts = new HashMap<>();
        changedCards = new HashMap<>();

        PersonDb personDb = new PersonDb();
        AccountDb accountDb = new AccountDb();
        CardDb cardDb = new CardDb();

        try {

            ArrayList<Employee> loadedEmployees = personDb.loadAllEmployees();
            ArrayList<Client> loadedClients = personDb.loadAllClients();


        } catch (SQLException err) {

            Logger log = Logger.getLogger();
            log.logMessage("Error while trying to load initial info from database: " + err.getMessage());
            return;
        }
    }
}
