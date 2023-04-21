package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class NamingServer {

  private static int port;

  public static void main(String[] args) throws Exception{
    boolean debug = false;

    System.out.println(NamingServer.class.getSimpleName());
    System.out.printf("Received %d Argument(s)%n", args.length);

    for (int i = 0; i < args.length; i++) {
      System.out.printf("args[%d] = %s%n", i, args[i]);
    }

    if(args.length == 1 && args[0].equals("-debug")){
      debug = true;
    }

    port = Integer.valueOf("5000");
    final BindableService impl = new NamingServerServiceImpl(debug);

    // Create a new server to listen on port.
    Server server = ServerBuilder.forPort(port).addService(impl).build();
    // Start the server.
    server.start();
    // Server threads are running in the background.
    System.out.println("Server started");

    // Do not exit the main thread. Wait until server is terminated.
    server.awaitTermination();
  }
}
