package ModelManager;

import java.util.*;

public class ClientsManager {

    public static HashMap<String, Client> clients;
    public static AbstractMap<String, Client> clientsMap;

    public static void arrangeByFunds(){

        clientsMap = new TreeMap<>((s1, s2) -> {

            Client c1 = clients.get(s1);
            Client c2 = clients.get(s2);

            double dif = c1.getClientFunds() - c2.getClientFunds();

            if(dif > 0)
                return 1;
            else if(dif == 0)
                return 0;
            else
                return -1;
        });

        for(Map.Entry<String, Client> client: clients.entrySet()){
            clientsMap.put(client.getKey(), client.getValue());
        }
    }

    public static void arrangeByCreditDebt(){

        clientsMap = new TreeMap<>((s1, s2) -> {

            Client c1 = clients.get(s1);
            Client c2 = clients.get(s2);

            double dif = c1.getCreditDebt() - c2.getCreditDebt();

            if(dif > 0)
                return 1;
            else if(dif == 0)
                return 0;
            else
                return -1;
        });

        for(Map.Entry<String, Client> client: clients.entrySet()){
            clientsMap.put(client.getKey(), client.getValue());
        }
    }

    public static void arrangeByName(){

        clientsMap = new TreeMap<>((s1, s2) -> {

            Client c1 = clients.get(s1);
            Client c2 = clients.get(s2);

            return (c1.getName() + c1.getSurname()).compareTo(c2.getName() + c2.getSurname());
        });

        for(Map.Entry<String, Client> client: clients.entrySet()){
            clientsMap.put(client.getKey(), client.getValue());
        }
    }

    public static void arrangeByRegistrationDate(){

        clientsMap = new TreeMap<>((s1, s2) -> {

            long a1 = clients.get(s1).getRegistrationDate().getTime();
            long a2 = clients.get(s2).getRegistrationDate().getTime();

            return Long.compare(a1, a2);
        });

        for(Map.Entry<String, Client> client: clients.entrySet()){
            clientsMap.put(client.getKey(), client.getValue());
        }
    }

    public static void arrangeCustom(Comparator<String> idBasedComparator){

        clientsMap = new TreeMap<>(idBasedComparator);

        for(Map.Entry<String, Client> client: clients.entrySet()){
            clientsMap.put(client.getKey(), client.getValue());
        }
    }

}
