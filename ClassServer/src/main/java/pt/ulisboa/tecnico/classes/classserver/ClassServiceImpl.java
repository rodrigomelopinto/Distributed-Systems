package pt.ulisboa.tecnico.classes.classserver;

import static io.grpc.Status.INVALID_ARGUMENT;

import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateRequest;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateResponse;

import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;
import java.util.Timer;
import java.util.TimerTask;

import io.grpc.stub.StreamObserver;

public class ClassServiceImpl extends ClassServerServiceGrpc.ClassServerServiceImplBase{
	private Class class1;
	public ClassServiceImpl(boolean debug, String type){
		class1 = Class.getInstance(debug,type);
	}

	/**
	 * The function takes in a PropagateStateRequest object, which contains a classState and a
	 * listUpdatesMap. The function then calls the setClassState function in the class1 class, which takes
	 * in the classState and listUpdatesMap and returns a code. The code is then used to create a
	 * PropagateStateResponse object, which is returned to the client
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send the response back to the
	 * client.
	 */
	@Override
	public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
        PropagateStateResponse response = PropagateStateResponse.newBuilder().setCode(class1.setClassState(request.getClassState(),request.getListUpdatesMap())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}