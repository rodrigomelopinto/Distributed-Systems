package pt.ulisboa.tecnico.classes.classserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.*;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import java.util.*;
import java.util.TimerTask;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class Gossip extends TimerTask{
    private Class class1;

    public Gossip(){
        class1 = Class.getInstance();
    }

    /**
     *  The method that is called when the timer is triggered.
     *  It sends the current state of the class to all the other servers
     */
    public void run(){
        if(class1.getState() == true && class1.getGossip() == true){
            String host = "localhost";
            int port = Integer.parseInt("5000");
            List<String> qualifiers = new ArrayList<>();

            try{
                ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

                NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);
                // a P server sends its state to S servers and vice-versa
                if(class1.getType().equals("S")){
                    qualifiers.add("P");
                }
                if(class1.getType().equals("P")){
                    qualifiers.add("S");
                }
                LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());

                channel.shutdownNow();

                if(servers.getResponseList().size()==0){
                    return;
                }
                ClassServerServiceGrpc.ClassServerServiceBlockingStub stubC;
                // Iterating through the list of servers and sending the current state of the class to
                // each of them.
                for(int i = 0; i < servers.getResponseList().size(); i++){
                    channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
                    stubC = ClassServerServiceGrpc.newBlockingStub(channel);
                    stubC.propagateState(PropagateStateRequest.newBuilder().setClassState(class1.getClassState()).putAllListUpdates(class1.getListUpdates()).build());
                    channel.shutdownNow();
                }
            }
            catch(StatusRuntimeException e){
                Status status = e.getStatus();
                System.out.println(status.getDescription());
            }
        }
    }
}
