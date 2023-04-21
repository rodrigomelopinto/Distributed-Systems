package pt.ulisboa.tecnico.classes.professor;

import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.classes.DateHandler;


public class Professor {

  private static final String LISTCLASS_CMD = "list";
	private static final String OPENENROLLMENTS_CMD = "openEnrollments";
	private static final String CLOSEENROLLMENTS_CMD = "closeEnrollments";
  private static final String CANCELENROLLMENT_CMD = "cancelEnrollment";
  private static final String EXIT_CMD = "exit";

  public static void main(String[] args) {
    //System.out.println(Professor.class.getSimpleName());
    //System.out.printf("Received %d Argument(s)%n", args.length);
    /*for (int i = 0; i < args.length; i++) {
      System.out.printf("args[%d] = %s%n", i, args[i]);
    }*/

    final String host = "localhost";
		final int port = 5000;

		Scanner scanner = new Scanner(System.in);
    List<String> qualifiers = new ArrayList<>();

    while(true){
      System.out.printf("%n> ");
			String line = scanner.next();

      if (EXIT_CMD.equals(line)) {
				scanner.close();
				break;
			}

      // The code for the list command. It first connects to the naming server and asks for the list of
      // servers that are available for the service "turmas". If there are no servers available, it
      // prints a message and continues. If there are servers available, it connects to one of them
      // randomly and asks for the list of students in the class. If the class is empty, it prints a
      // message and continues. If the class is not empty, it prints the list of students and other class features.
      if (LISTCLASS_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          int randomNum = ThreadLocalRandom.current().nextInt(0, servers.getResponseList().size());
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(randomNum).split(":")[0], Integer.parseInt(servers.getResponseList().get(randomNum).split(":")[1])).usePlaintext().build();
          final ProfessorServiceGrpc.ProfessorServiceBlockingStub stubP = ProfessorServiceGrpc.newBlockingStub(channel);

          ListClassResponse response = stubP.listClass(ListClassRequest.newBuilder().build());
          if(response.getCodeValue()==6){
            System.out.println(Stringify.format(response.getCode()));
            channel.shutdownNow();
            continue;
          }
          System.out.println(Stringify.format(response.getClassState()));
          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      // Opening enrollments for the class.
      if (OPENENROLLMENTS_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add("P");
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(0).split(":")[0], Integer.parseInt(servers.getResponseList().get(0).split(":")[1])).usePlaintext().build();
          final ProfessorServiceGrpc.ProfessorServiceBlockingStub stubP = ProfessorServiceGrpc.newBlockingStub(channel);

          OpenEnrollmentsResponse response = stubP.openEnrollments(OpenEnrollmentsRequest.newBuilder().setCapacity(scanner.nextInt()).build());
          System.out.println(Stringify.format(response.getCode()));
          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      // Closing the enrollments for the class.
      if (CLOSEENROLLMENTS_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add("P");
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(0).split(":")[0], Integer.parseInt(servers.getResponseList().get(0).split(":")[1])).usePlaintext().build();
          final ProfessorServiceGrpc.ProfessorServiceBlockingStub stubP = ProfessorServiceGrpc.newBlockingStub(channel);

          CloseEnrollmentsResponse response = stubP.closeEnrollments(CloseEnrollmentsRequest.newBuilder().setTime(DateHandler.toISOString(LocalDateTime.now())).build());
          System.out.println(Stringify.format(response.getCode()));

          channel.shutdownNow();
        }
        catch(StatusRuntimeException e){
          Status status = e.getStatus();
          System.out.println(status.getDescription());
        }
			}

      // The code for the cancelEnrollment command. It first connects to the naming server and asks for
      // the list of
      // servers that are available for the service "turmas" with the qualifier "P". If there are no
      // servers available, it
      // prints a message and continues. If there are servers available, it connects to one of them
      // randomly and asks for the
      // cancellation of the enrollment of the student with the id given by the professor. If the student is
      // not enrolled, it prints
      // a message and continues. If the student is enrolled, it cancels the enrollment and prints a
      // message.
      if (CANCELENROLLMENT_CMD.equals(line)) {
        try{
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          qualifiers.add("P");
          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          qualifiers.remove(0);
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(0).split(":")[0], Integer.parseInt(servers.getResponseList().get(0).split(":")[1])).usePlaintext().build();
          final ProfessorServiceGrpc.ProfessorServiceBlockingStub stubP = ProfessorServiceGrpc.newBlockingStub(channel);

          CancelEnrollmentResponse response = stubP.cancelEnrollment(CancelEnrollmentRequest.newBuilder().setStudentId(scanner.next()).build());
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
