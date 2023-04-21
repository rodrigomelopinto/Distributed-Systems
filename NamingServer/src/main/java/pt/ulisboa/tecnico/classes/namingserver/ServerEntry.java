package pt.ulisboa.tecnico.classes.namingserver;
import java.util.*;

/**
 * It's a class that represents a server entry
 */
public class ServerEntry {
    String host;
    List<String> qualifiers;

    public ServerEntry(String host, List<String> qualifiers){
        this.host = host;
        this.qualifiers = qualifiers;
    }

    /**
     * This function returns the host of the ServerEntry
     * 
     * @return The host variable is being returned.
     */
    public String getHost(){
        return this.host;
    }

    /**
     * This function returns a list of strings that are the qualifiers of the current object (P or S)
     * 
     * @return A list of strings
     */
    public List<String> getQualifiers(){
        return this.qualifiers;
    }
}
