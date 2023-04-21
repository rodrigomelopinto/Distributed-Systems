package pt.ulisboa.tecnico.classes.namingserver;
import java.util.*;

/**
 * It's a class that holds a list of ServerEntry objects
 */
public class ServiceEntry {
    String service;
    List<ServerEntry> entries;

    public ServiceEntry(String serviceName){
        this.service = serviceName;
        this.entries = new ArrayList<>();
    }

    /**
     * This function returns a list of ServerEntry objects.
     * 
     * @return A list of ServerEntry objects.
     */
    public List<ServerEntry> getEntries(){
        return this.entries;
    }

    /**
     * This function adds a ServerEntry to the entries list.
     * 
     * @param entry The entry to add to the list.
     */
    public void addEntry(ServerEntry entry){
        this.entries.add(entry);
    }
}
