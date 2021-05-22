package AppManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {

    private static Logger instance;
    private BufferedWriter writer;

    public static Logger getLogger(){

        try {
            if (instance == null) {
                instance = new Logger();
                instance.writer = new BufferedWriter(new FileWriter("defaultLogFile.txt"));
            }
        }
        catch(IOException err) {
            System.out.println("WARNING: could not return logger object");
        }

        return instance;
    }

    public void changeLogFile(String newFileName){

        try {
            writer.close();
            writer = new BufferedWriter(new FileWriter(newFileName));
        }
        catch(IOException err){
            System.out.println("WARNING: could not change logger state: " + err);
        }
    }

    public void logMessage(String toLog){

        try{
            String toWrite = (new Date(System.currentTimeMillis())).toString() + " >> " + toLog;
            writer.write(toWrite);
            writer.write('\n');
        }
        catch(IOException err){
            System.out.println("WARNING: could not log in file: " + err);
        }
    }

    public void endLogging(){
        try{
            writer.close();
        }
        catch(IOException err){
            System.out.println("WARNING: could not close logging (log file may look empty)");
        }
    }
}
