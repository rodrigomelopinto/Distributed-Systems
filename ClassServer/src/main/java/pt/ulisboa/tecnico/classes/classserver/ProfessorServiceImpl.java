package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.ActivateRequest;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateRequest;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.CancelEnrollmentRequest;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.CancelEnrollmentResponse;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.OpenEnrollmentsRequest;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.OpenEnrollmentsResponse;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.CloseEnrollmentsRequest;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.CloseEnrollmentsResponse;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.ListClassRequest;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.ListClassResponse;

import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;

import io.grpc.stub.StreamObserver;

public class ProfessorServiceImpl extends ProfessorServiceGrpc.ProfessorServiceImplBase{
	private Class class1;
	public ProfessorServiceImpl(boolean debug, String type){
		class1 = Class.getInstance(debug, type);
	}

	/**
	 * It returns the class state and the code of the class.
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
	 * The function takes in a request object and a responseObserver object. It then calls the
	 * openEnrollments function in the class1 object, passing in the capacity from the request object. It
	 * then creates a response object using the code returned from the openEnrollments function. It then
	 * calls the onNext function on the responseObserver object, passing in the response object. It then
	 * calls the onCompleted function on the responseObserver object
	 * 
	 * @param request The request object that the client sends to the server.
	 * @param responseObserver This is the object that will be used to send the response back to the
	 * client.
	 */
	public void openEnrollments(OpenEnrollmentsRequest request, StreamObserver<OpenEnrollmentsResponse> responseObserver) {
        OpenEnrollmentsResponse response = OpenEnrollmentsResponse.newBuilder().setCode(class1.openEnrollments(request.getCapacity())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * Request to close the enrollments in a class
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void closeEnrollments(CloseEnrollmentsRequest request, StreamObserver<CloseEnrollmentsResponse> responseObserver) {
		class1.getCloseTime(request.getTime());
        CloseEnrollmentsResponse response = CloseEnrollmentsResponse.newBuilder().setCode(class1.closeEnrollments()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * It cancels the enrollment of a student.
	 * 
	 * @param request The request object that the client sent to the server.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void cancelEnrollment(CancelEnrollmentRequest request, StreamObserver<CancelEnrollmentResponse> responseObserver) {
        CancelEnrollmentResponse response = CancelEnrollmentResponse.newBuilder().setCode(class1.cancelEnrollment(request.getStudentId())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
