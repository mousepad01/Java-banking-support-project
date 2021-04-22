package AppManager;

import java.util.Comparator;

public class CreditDebtComparator implements Comparator<Client> {

    public int compare(Client c1, Client c2){

        double dif = c1.getCreditDebt() - c2.getCreditDebt();

        if(dif > 0)
            return 1;
        else if(dif == 0)
            return 0;
        else
            return -1;
    }
}
