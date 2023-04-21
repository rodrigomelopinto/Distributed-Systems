package pt.ulisboa.tecnico.classes.namingserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import java.util.logging.Logger;

public class NamingServices {
    private static final Logger LOGGER = Logger.getLogger(NamingServices.class.getName());

    ConcurrentHashMap<String,ServiceEntry> serviceMap;
    boolean debug;

    public NamingServices(boolean debug){
        serviceMap = new ConcurrentHashMap<>();
        this.debug = debug;
    }

    /**
     * If the service name is not in the service map, create a new service entry and add it to the
     * service map. If the service name is in the service map, add the server entry to the service
     * entry
     * 
     * @param serviceName The name of the service that is being registered. (turmas)
     * @param host The hostname of the server (localhost)
     * @param qualifiers A list of strings that describe the server. The only qualifiers that are
     * useful describe if the server is Primary (P) or Secundary (S).
     */
    public synchronized void register(String serviceName, String host, List<String> qualifiers){
        if(!serviceMap.containsKey(serviceName)){
            ServiceEntry service = new ServiceEntry(serviceName);
            ServerEntry entry = new ServerEntry(host, qualifiers);
            service.addEntry(entry);
            serviceMap.put(serviceName,service);
        }
        else{
            ServerEntry entry = new ServerEntry(host, qualifiers);
            serviceMap.get(serviceName).addEntry(entry);
        }

        if(this.debug == true){
            LOGGER.info("New server was registered;");
        }
    }

    /**
     * This function takes in a service name and a list of qualifiers and returns a list of servers
     * that match the service name and qualifiers
     * 
     * @param serviceName The name of the service you want to look up. (turmas)
     * @param qualifiers a list of strings that are the qualifiers that the client is looking for.
     * @return A list of servers that are registered to the service name and qualifiers. (P or S)
     */
    public synchronized List<String> lookup(String serviceName, List<String> qualifiers){
        List<String> servers = new ArrayList<>();
        if(!serviceMap.containsKey(serviceName)){
            if(this.debug == true){
                LOGGER.info("Server info was sent;");
            }
            return servers;
        }
        List<ServerEntry> entries = serviceMap.get(serviceName).getEntries();
        if(qualifiers.size()==0){
            for(int i = 0; i < entries.size(); i++){
                servers.add(entries.get(i).getHost());
            }
            if(this.debug == true){
                LOGGER.info("Server info was sent;");
            }
            return servers;
        }
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getQualifiers().contains(qualifiers.get(0))){
                servers.add(entries.get(i).getHost());
            }
        }
        if(qualifiers.size() == 2){
            for(int i = 0; i < entries.size(); i++){
                if(entries.get(i).getQualifiers().contains(qualifiers.get(1))){
                    servers.add(entries.get(i).getHost());
                }
            }
        }
        if(this.debug == true){
            LOGGER.info("Server info was sent;");
        }
        return servers;
    }

    /**
     * This function removes a server from the service map
     * 
     * @param serviceName The name of the service you want to delete a server from.
     * @param host The hostname of the server to be deleted.
     */
    public synchronized void delete(String serviceName, String host){
        List<ServerEntry> entries = serviceMap.get(serviceName).getEntries();
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getHost().equals(host)){
                entries.remove(i);
            }
        }
        if(this.debug == true){
            LOGGER.info("A server was removed;");
        }
    }
}
