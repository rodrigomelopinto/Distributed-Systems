package pt.ulisboa.tecnico.classes.classserver;

import java.util.ArrayList;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.*;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.*;
import io.grpc.StatusRuntimeException;
import io.grpc.Status;


public class ClassServer {

  private static int port;

  public static void main(String[] args) throws Exception{
	boolean debug = false;

    System.out.println(ClassServer.class.getSimpleName());
    System.out.printf("Received %d Argument(s)%n", args.length);

    for (int i = 0; i < args.length; i++) {
      System.out.printf("args[%d] = %s%n", i, args[i]);
    }

	port = Integer.valueOf(args[1]);
	if(args.length == 4 && args[3].equals("-debug")){
		debug = true;
	}
	final BindableService impl = new ClassServiceImpl(debug,args[2]);
	final BindableService impl1 = new AdminServiceImpl(debug,args[2]);
	final BindableService impl2 = new ProfessorServiceImpl(debug,args[2]);
	final BindableService impl3 = new StudentServiceImpl(debug,args[2]);

	final String hostN = "localhost";
	final int portN = Integer.parseInt("5000");

	try{
		ManagedChannel channel = ManagedChannelBuilder.forAddress(hostN, portN).usePlaintext().build();

		NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

		String host = args[0]+ ":" + args[1];
		List<String> qualifiers = new ArrayList<>();
		qualifiers.add(args[2]);

		// It sends a request to the naming server to register the service "turmas" in the host
		stub.register(RegisterRequest.newBuilder().setService("turmas").setHost(host).addAllQualifiers(qualifiers).build());
		qualifiers.remove(0);
		channel.shutdownNow();

		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(port).addService(impl).addService(impl1).addService(impl2).addService(impl3).build();
		// Start the server.
		server.start();
		// Server threads are running in the background.
		System.out.println("Server started");

		Runtime.getRuntime().addShutdownHook(new ShutdownServer(host));
		
		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
	}
	catch(StatusRuntimeException e){
		Status status = e.getStatus();
		System.out.println(status.getDescription());
	}
	
  }

  private static class ShutdownServer extends Thread {
	private String host;

	public ShutdownServer(String host){
		this.host = host;
	}

    /**
	 * It sends a request to the naming server to delete the service "turmas" from the host "localhost"
	 */
	@Override
    public void run() {
		final String hostN = "localhost";
		final int portN = Integer.parseInt("5000");
		
		try{
			ManagedChannel channel = ManagedChannelBuilder.forAddress(hostN, portN).usePlaintext().build();

			NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);
			stub.delete(DeleteRequest.newBuilder().setService("turmas").setHost(host).build());

			channel.shutdownNow();
		}
        catch(StatusRuntimeException e){
			Status status = e.getStatus();
			System.out.println(status.getDescription());
        }
    }
  }


}