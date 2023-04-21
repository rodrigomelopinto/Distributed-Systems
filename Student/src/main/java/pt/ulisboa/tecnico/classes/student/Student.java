package pt.ulisboa.tecnico.classes.student;

import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.DateHandler;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;
import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Student {
  private static final String LISTCLASS_CMD = "list";
	private static final String ENROLL_CMD = "enroll";
  private static final String EXIT_CMD = "exit";

  public static void main(String[] args) {

    final String host = "localhost";
		final int port = 5000;
    String num;
    String studentId;
    num = "";
    int i = 0;

		Scanner scanner = new Scanner(System.in);
    List<String> qualifiers = new ArrayList<>();

    studentId = args[0];
    // Checking if the student id is valid.
    if(studentId.length() != 9 || studentId.startsWith("aluno") == false){
      System.out.println("Invalid student id");
      scanner.close();
      return;
    }
    for(i = 5; i < studentId.length(); i++){
      num = num + studentId.charAt(i);
    }
    try{
      Integer.parseInt(num);
    }
    catch(Exception e){
      System.out.println("Invalid student id");
      scanner.close();
      return;
    }

    while(true){
      System.out.printf("%n> ");
			String line = scanner.next();

      if (EXIT_CMD.equals(line)) {
				scanner.close();
				break;
			}

      // The code for the student to list the class.
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

          // Getting a random server from the list of servers that the naming server returned.
          int randomNum = ThreadLocalRandom.current().nextInt(0, servers.getResponseList().size());
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(randomNum).split(":")[0], Integer.parseInt(servers.getResponseList().get(randomNum).split(":")[1])).usePlaintext().build();
          final StudentServiceGrpc.StudentServiceBlockingStub stubS = StudentServiceGrpc.newBlockingStub(channel);

          // Sending a request to the server to list all the classes.
          ListClassResponse response = stubS.listClass(ListClassRequest.newBuilder().build());

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

      // The code for the student to enroll in a class.
      if (ENROLL_CMD.equals(line)) {
        String name1;
        
        studentId = args[0];
        name1= args[1];
        i=2;
        while(i<args.length){
          name1 = name1 + " " + args[i];
          i++;
        }
        
        try{
          // Creating a channel to the naming server and then it is sending a lookup request to the
          // naming server.
          ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

          final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

          LookupResponse servers = stub.lookup(LookupRequest.newBuilder().setService("turmas").addAllQualifiers(qualifiers).build());
          channel.shutdownNow();

          if(servers.getResponseList().size()==0){
            System.out.println("No servers available");
            continue;
          }

          // Getting a random server from the list of servers that the naming server returned.
          int randomNum = ThreadLocalRandom.current().nextInt(0, servers.getResponseList().size());
          channel = ManagedChannelBuilder.forAddress(servers.getResponseList().get(randomNum).split(":")[0], Integer.parseInt(servers.getResponseList().get(randomNum).split(":")[1])).usePlaintext().build();
          final StudentServiceGrpc.StudentServiceBlockingStub stubS = StudentServiceGrpc.newBlockingStub(channel);

          // Creating a new EnrollRequest with the studentId and studentName and sending it to the
          // server.
          EnrollResponse response = stubS.enroll(EnrollRequest.newBuilder().setStudent(
              ClassesDefinitions.Student.newBuilder().setStudentId(studentId).setStudentName(name1)).setTime(DateHandler.toISOString(LocalDateTime.now())).build());
          
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
