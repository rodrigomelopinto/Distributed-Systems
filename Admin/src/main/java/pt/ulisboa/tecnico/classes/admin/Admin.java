package pt.ulisboa.tecnico.classes.admin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Admin {

  private static final String ACTIVATE_CMD = "activate";
	private static final String DEACTIVATE_CMD = "deactivate";
	private static final String DUMP_CMD = "dump";
  private static final String ACTIVATEGOSSIP_CMD = "activateGossip";
  private static final String DEACTIVATEGOSSIP_CMD = "deactivateGossip";
  private static final String GOSSIP_CMD = "gossip";
  private static final String EXIT_CMD = "exit";

  

  public static void main(String[] args) {

    // Creating a variable called host and setting it to localhost. It is also creating a variable
    // called port and setting it to 5000. They will be used to connect to the NamingServer

    final String host = "localhost";
		final int port = Integer.parseInt("5000");

		Scanner scanner = new Scanner(System.in);
    List<String> qualifiers = new ArrayList<>();

    while(true){
      System.out.printf("%n> ");
			String line = scanner.next();

      if (EXIT_CMD.equals(line)) {
				scanner.close();
				break;
			}

      if (ACTIVATE_CMD.equals(line)) {
        try{
          // Creating a channel to the server.
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          // Creating a stub to the naming server.
          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add(scanner.next());
          // Looking up for a service called "turmas" and it is using the qualifiers to filter the
          // results.
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
        
          qualifiers.remove(0);
          channel.shutdownNow();

          // The below code is checking if the port entered by the user is valid or not and if theres servers.
          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          // Creating a channel to the server.
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Creating a stub for the service and then calling the activate method to the service on the stub.
          ActivateResponse response = stubA.activate(ActivateRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      if (DEACTIVATE_CMD.equals(line)) {
        try{
          // Creating a channel to the server.
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          // Creating a stub for the NamingServerService.
          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          // Looking up for a service called "turmas" and it is adding the qualifiers to the lookup
          // request.
          qualifiers.add(scanner.next());
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          // The below code is checking if the port entered by the user is valid or not and if theres servers.
          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          // Creating a channel to the server and then creating a stub to the server.
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], 
                        Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Deactivating the service.
          DeactivateResponse response = stubA.deactivate(DeactivateRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      if (DUMP_CMD.equals(line)) {
        try{

          // Creating a channel and stub to the NamingServer.
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          // Looking up for a service called "turmas" and adding the qualifier to the list of
          // qualifiers.
          qualifiers.add(scanner.next());
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          // The below code is checking if the port entered by the user is valid or not and if theres servers.
          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          // Creating a channel to the server and then creating a stub to the server.
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], 
                      Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Calling the dump method on the stubA object.
          DumpResponse response = stubA.dump(DumpRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getClassState()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      if (ACTIVATEGOSSIP_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add(scanner.next());
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          // Creating a channel to the server and then creating a stub to the server.
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Activating gossip on the node stubA.
          ActivateGossipResponse response = stubA.activateGossip(ActivateGossipRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      if (DEACTIVATEGOSSIP_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add(scanner.next());
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Deactivating gossip on the node.
          DeactivateGossipResponse response = stubA.deactivateGossip(DeactivateGossipRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      if (GOSSIP_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add(scanner.next());
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          String port1;
          port1 = scanner.next();
          int i;
          for(i = 0; i < servers.getResponseList().size(); i++){
            if(servers.getResponseList().get(i).split(":")[1].equals(port1)){
              break;
            }
          }

          if(i == servers.getResponseList().size()){
            System.out.println("Invalid port");
            continue;
          }

          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(i).split(":")[0], Integer.parseInt(servers.getResponseList().get(i).split(":")[1])).usePlaintext().build();
          final AdminServiceGrpc.AdminServiceBlockingStub stubA = AdminServiceGrpc.newBlockingStub(channel);

          // Sending a gossip request to the server and printing the response.
          GossipResponse response = stubA.gossip(GossipRequest.newBuilder().build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}
    }
  }
}
