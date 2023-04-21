package pt.ulisboa.tecnico.classes.classserver;

import static io.grpc.Status.INVALID_ARGUMENT;

import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.ActivateRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.ActivateResponse;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DeactivateRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DeactivateResponse;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DumpRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DumpResponse;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.ActivateGossipRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.ActivateGossipResponse;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DeactivateGossipRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.DeactivateGossipResponse;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.GossipRequest;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.GossipResponse;

import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;

import io.grpc.stub.StreamObserver;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase{
	private Class class1;
	public AdminServiceImpl(boolean debug, String type){
		class1 = Class.getInstance(debug, type);
	}

	/**
	 * The function takes in an ActivateRequest object, and returns an ActivateResponse object
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	@Override
	public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        ActivateResponse response = ActivateResponse.newBuilder().setCode(class1.activate()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * The function takes in a DeactivateRequest object and a StreamObserver object. It then calls the
	 * deactivate() function in the class1 class and returns the result in a DeactivateResponse object
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        DeactivateResponse response = DeactivateResponse.newBuilder().setCode(class1.deactivate()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * The function takes a DumpRequest object, calls the dump() function of class1, and returns a
	 * DumpResponse object with the return value of dump() and the class state
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send the response back to the
	 * client.
	 */
	public void dump(DumpRequest request, StreamObserver<DumpResponse> responseObserver) {
        DumpResponse response = DumpResponse.newBuilder().setCode(class1.dump()).setClassState(class1.getClassState()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * It activates the gossip protocol.
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void activateGossip(ActivateGossipRequest request, StreamObserver<ActivateGossipResponse> responseObserver) {
        ActivateGossipResponse response = ActivateGossipResponse.newBuilder().setCode(class1.activateGossip()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * It deactivates the gossip protocol.
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send a response back to the client.
	 */
	public void deactivateGossip(DeactivateGossipRequest request, StreamObserver<DeactivateGossipResponse> responseObserver) {
        DeactivateGossipResponse response = DeactivateGossipResponse.newBuilder().setCode(class1.deactivateGossip()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * A function that is called when a client calls the gossip function. It calls the Gossip function in
	 * class1 and returns the result.
	 * 
	 * @param request The request object that was sent by the client.
	 * @param responseObserver The object that will be used to send a response back to the client.
	 */
	public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
        GossipResponse response = GossipResponse.newBuilder().setCode(class1.Gossip()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
