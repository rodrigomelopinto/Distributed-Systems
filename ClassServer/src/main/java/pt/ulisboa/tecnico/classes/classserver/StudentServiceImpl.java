package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.ListClassRequest;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.ListClassResponse;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.EnrollRequest;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.EnrollResponse;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;

import io.grpc.stub.StreamObserver;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase{
	private Class class1;
	public StudentServiceImpl(boolean debug, String type){
		class1 = Class.getInstance(debug, type);
	}

	/**
	 * It returns the class state.
	 * 
	 * @param request The request object that was sent by the client.
	 * @param responseObserver The responseObserver is a StreamObserver object that is used to send a
	 * response back to the client.
	 */
	@Override
	public void listClass(ListClassRequest request, StreamObserver<ListClassResponse> responseObserver) {
        ListClassResponse response = ListClassResponse.newBuilder().setCode(class1.listClass()).setClassState(class1.getClassState()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * The function takes in a request from the client to enroll in the class, and returns a response to the client
	 * 
	 * @param request The request parameter is the request sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void enroll(EnrollRequest request, StreamObserver<EnrollResponse> responseObserver) {
		class1.getUpdateTime(request.getTime(), request.getStudent());
        EnrollResponse response = EnrollResponse.newBuilder().setCode(class1.enroll(request.getStudent())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}